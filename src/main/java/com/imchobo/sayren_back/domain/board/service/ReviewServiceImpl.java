package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.review.ReviewCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.mapper.ReviewMapper;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService{
  private final BoardRepository boardRepository;
  private final ReviewMapper reviewMapper;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public Long register(ReviewCreateRequestDTO dto) {
    // DTO -> Entity
    Board board = reviewMapper.toEntity(dto);

    // 작성자 (로그인 사용자)
    board.setMember(memberRepository.findById(SecurityUtil.getMemberEntity().getId())
            .orElseThrow(() -> new RuntimeException("회원 없음")));

    // 카테고리 (REVIEW 고정)
    Category category = categoryRepository.findByType(CategoryType.REVIEW)
            .orElseThrow(() -> new RuntimeException("리뷰 카테고리 없음"));
    board.setCategory(category);

    // 상품 필수
    board.setProduct(productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("상품 없음")));

    // 저장 후 PK 반환
    return boardRepository.save(board).getId();
  }

  @Override
  public void modify(Long id, ReviewModifyRequestDTO dto) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글 없음"));

    // DTO 값으로 엔티티 업데이트
    reviewMapper.updateEntity(board, dto);

    // 상품 수정도 반영
    board.setProduct(productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("상품 없음")));
  }

  @Override
  public void delete(Long id) {
    boardRepository.deleteById(id);
  }

  @Override
  public ReviewDetailsResponseDTO read(Long id) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글 없음"));
    return reviewMapper.toDetailsDTO(board);
  }

  @Override
  public List<ReviewListResponseDTO> list() {
    return boardRepository.findByCategoryType(CategoryType.REVIEW).stream()
            .map(reviewMapper::toListDTO)
            .toList();
  }
}
