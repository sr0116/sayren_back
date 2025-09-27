package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.qna.QnaCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.QnaMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class QnaServiceImpl implements QnaService{
  private final BoardRepository boardRepository;
  private final QnaMapper qnaMapper;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Long register(QnaCreateRequestDTO dto) {
    // DTO -> Entity 변환
    Board board = qnaMapper.toEntity(dto);

    // 작성자 = 로그인 사용자
    board.setMember(memberRepository.findById(SecurityUtil.getMemberEntity().getId())
            .orElseThrow(() -> new RuntimeException("회원 없음")));

    // 카테고리 = QNA 고정
    Category category = categoryRepository.findByType(CategoryType.QNA)
            .orElseThrow(() -> new RuntimeException("QNA 카테고리 없음"));
    board.setCategory(category);

    // 저장 후 PK 반환
    return boardRepository.save(board).getId();
  }

  @Override
  public void modify(Long id, QnaModifyRequestDTO dto) {
    // 수정 대상 조회
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글 없음"));

    // Mapper로 엔티티 업데이트 (title, content, isSecret 등 반영)
    qnaMapper.updateEntity(board, dto);
  }

  @Override
  public void delete(Long id) {
    boardRepository.deleteById(id);
  }

  @Override
  public QnaDetailsResponseDTO read(Long id) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글 없음"));
    return qnaMapper.toDetailsDTO(board);
  }

  @Override
  public List<QnaListResponseDTO> list() {
    return boardRepository.findByCategoryType(CategoryType.QNA).stream()
            .map(qnaMapper::toListDTO)
            .toList();
  }
}
