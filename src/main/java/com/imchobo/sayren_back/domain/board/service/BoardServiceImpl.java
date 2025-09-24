package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
//import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
//import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.BoardMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
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
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;
  private final BoardMapper boardMapper;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Long register(BoardRequestDTO dto) {
    // DTO → 엔티티
    Board board = boardMapper.toEntity(dto);

    // 작성자 세팅 (현재 로그인 사용자)
    Member member = memberRepository.findById(SecurityUtil.getMemberEntity().getId())
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));
    board.setMember(member);

    // 카테고리 세팅
    Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("카테고리가 없습니다"));
    board.setCategory(category);

    // 저장 후 ID 반환
    return boardRepository.save(board).getId();
  }

  @Override
  public void modify(Long id, BoardRequestDTO dto) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다"));

    // Mapper로 값 업데이트
    boardMapper.updateEntity(board, dto);
  }

  @Override
  public void delete(Long id) {
    boardRepository.deleteById(id);
  }

  @Override
  public BoardResponseDTO read(Long id) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다"));
    return boardMapper.toResponseDTO(board);
  }

  @Override
  public List<BoardResponseDTO> list() {
    return boardRepository.findAll().stream()
            .map(boardMapper::toResponseDTO)
            .toList();
  }
}
