package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.BoardCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.BoardMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.board.service.BoardService;
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
  private final ProductRepository productRepository;

  @Override
  public Long register(BoardCreateRequestDTO dto) {
    // 현재 로그인 사용자 ID 가져오기
    Long memberId = SecurityUtil.getMemberEntity().getId();

    // DTO -> Entity
    Board board = boardMapper.toEntity(dto);

    // 작성자 세팅
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));
    board.setMember(member);

    // 카테고리 세팅
    Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("카테고리가 없습니다"));
    board.setCategory(category);
    if (dto.getProductId() != null) {
      Product product = productRepository.findById(dto.getProductId())
              .orElseThrow(() -> new RuntimeException("상품이 없습니다"));
      board.setProduct(product);
    }


    // 저장
    Board saved = boardRepository.save(board);
    return saved.getId();
  }

  @Override
  public void modify(BoardModifyRequestDTO dto) {
    Board board = boardRepository.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("게시글이 없습니다"));

    // 수정할 값 반영
    board.setTitle(dto.getTitle());
    board.setContent(dto.getContent());
    board.setSecret(dto.isSecret());

    // 카테고리 바꾸는 경우
    if (dto.getId() != null) {
      Category category = categoryRepository.findById(dto.getId())
              .orElseThrow(() -> new RuntimeException("카테고리가 없습니다"));
      board.setCategory(category);
    }

    boardRepository.save(board);
  }

  @Override
  public void delete(Long boardId) {
    boardRepository.deleteById(boardId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BoardListResponseDTO> list() {
    return boardRepository.findAll().stream()
            .map(boardMapper::toListDTO)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BoardDetailsResponseDTO read(Long boardId) {
    Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new RuntimeException("게시글이 없습니다"));
    return boardMapper.toDetailsDTO(board);
  }

//  @Override
//  public List<BoardListResponseDTO> listByCategory(String category) {
//    CategoryType type = CategoryType.valueOf(category.toUpperCase());
//    return boardRepository.findByCategoryType(type).stream()
//            .map(boardMapper::toListDTO)
//            .toList();
//  }
}
