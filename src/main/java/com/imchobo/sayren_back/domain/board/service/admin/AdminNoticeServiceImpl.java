package com.imchobo.sayren_back.domain.board.service.admin;

import com.imchobo.sayren_back.domain.board.dto.notice.NoticeCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.NoticeMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AdminNoticeServiceImpl implements AdminNoticeService {
  private final BoardRepository boardRepository;
  private final NoticeMapper noticeMapper;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Long register(NoticeCreateRequestDTO dto) {
    Board board = noticeMapper.toEntity(dto);

    // 작성자 = 로그인 사용자
    board.setMember(memberRepository.findById(SecurityUtil.getMemberEntity().getId())
            .orElseThrow(() -> new RuntimeException("회원 없음")));

    // 카테고리 = NOTICE 고정
    Category category = categoryRepository.findByType(CategoryType.NOTICE)
            .orElseThrow(() -> new RuntimeException("공지 카테고리 없음"));
    board.setCategory(category);

    return boardRepository.save(board).getId();
  }

  @Override
  public void modify(Long id, NoticeModifyRequestDTO dto) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("공지 없음"));

    noticeMapper.updateEntity(board, dto);
  }

  @Override
  public void delete(Long id) {
    boardRepository.deleteById(id);
  }

  @Override
  public NoticeDetailsResponseDTO read(Long id) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("공지 없음"));
    return noticeMapper.toDetailsDTO(board);
  }

  @Override
  public List<NoticeListResponseDTO> list() {
    return boardRepository.findByCategoryType(CategoryType.NOTICE).stream()
            .map(noticeMapper::toListDTO)
            .toList();
  }

  @Override
  public PageResponseDTO<NoticeListResponseDTO, Board> getList(PageRequestDTO requestDTO) {

    Page<Board> result = boardRepository.findByCategoryType(CategoryType.NOTICE, requestDTO.getPageable());

    return PageResponseDTO.of(result, noticeMapper::toListDTO);
  }
}
