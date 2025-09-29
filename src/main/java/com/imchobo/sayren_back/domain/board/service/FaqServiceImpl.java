package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.FaqMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class FaqServiceImpl implements  FaqService {
  private final BoardRepository boardRepository;
  private final FaqMapper faqMapper;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Long register(FaqCreateRequestDTO dto) {
    Board board = faqMapper.toEntity(dto);

    // 작성자 = 로그인 사용자
    board.setMember(memberRepository.findById(SecurityUtil.getMemberEntity().getId())
            .orElseThrow(() -> new RuntimeException("회원 없음")));

    // 카테고리 = FAQ 고정
    Category category = categoryRepository.findByType(CategoryType.FAQ)
            .orElseThrow(() -> new RuntimeException("FAQ 카테고리 없음"));
    board.setCategory(category);

    return boardRepository.save(board).getId();
  }

  @Override
  public void modify(Long id, FaqModifyRequestDTO dto) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FAQ 글 없음"));

    faqMapper.updateEntity(board, dto);
  }

  @Override
  public void delete(Long id) {
    boardRepository.deleteById(id);
  }

  @Override
  public FaqDetailsResponseDTO read(Long id) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FAQ 글 없음"));
    return faqMapper.toDetailsDTO(board);
  }

  @Override
  public List<FaqListResponseDTO> list() {
    return boardRepository.findByCategoryType(CategoryType.FAQ).stream()
            .map(faqMapper::toListDTO)
            .toList();
  }

  @Override
  public PageResponseDTO<FaqListResponseDTO> getList(PageRequestDTO requestDTO) {
    Pageable pageable = PageRequest.of(
            requestDTO.getPage() - 1,
            requestDTO.getSize(),
            Sort.by("id").descending()
    );

    Page<Board> result = boardRepository.findByCategoryType(
            CategoryType.FAQ, pageable
    );

    List<FaqListResponseDTO> dtoList = result.getContent().stream()
            .map(faqMapper::toListDTO)
            .toList();

    // 페이지 계산
    int totalPage = result.getTotalPages();
    int page = requestDTO.getPage();
    int end = (int) (Math.ceil(page / 10.0)) * 10;
    int start = end - 9;
    end = Math.min(totalPage, end);

    boolean prev = start > 1;
    boolean next = totalPage > end;

    int totalCount = (int) boardRepository.countByCategoryType(CategoryType.FAQ);

    List<Integer> pageList =
            java.util.stream.IntStream.rangeClosed(start, end).boxed().toList();

    return PageResponseDTO.<FaqListResponseDTO>builder()
            .list(dtoList)
            .page(page)
            .size(requestDTO.getSize())
            .total(totalCount)
            .totalPage(totalPage)
            .start(start)
            .end(end)
            .prev(prev)
            .next(next)
            .pageList(pageList)
            .build();
  }
}
