package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.PageResponseDTO;
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
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
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
  @Transactional
  public void modify(Long id, ReviewModifyRequestDTO dto) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글 없음"));

    // 제목/내용 등만 매퍼로 업데이트
    reviewMapper.updateEntity(board, dto);

    // 상품 따로 세팅
    if (dto.getProductId() != null) {
      Product product = productRepository.findById(dto.getProductId())
              .orElseThrow(() -> new RuntimeException("상품 없음"));
      board.setProduct(product);
    }
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

  @Override
  public PageResponseDTO<ReviewListResponseDTO> getList(PageRequestDTO requestDTO) {
    Pageable pageable = PageRequest.of(
            requestDTO.getPage() - 1,
            requestDTO.getSize(),
            Sort.by("id").descending()
    );

    Page<Board> result = boardRepository.findByCategoryType(
            CategoryType.REVIEW, pageable
    );

    int totalCount = (int) boardRepository.countByCategoryType(CategoryType.REVIEW);

    List<ReviewListResponseDTO> dtoList = result.getContent().stream()
            .map(reviewMapper::toListDTO)
            .toList();

    // 페이지 계산
    int totalPage = result.getTotalPages();
    int page = requestDTO.getPage();
    int end = (int) (Math.ceil(page / 10.0)) * 10;
    int start = end - 9;
    end = Math.min(totalPage, end);

    boolean prev = start > 1;
    boolean next = totalPage > end;

    List<Integer> pageList =
            java.util.stream.IntStream.rangeClosed(start, end).boxed().toList();

    return PageResponseDTO.<ReviewListResponseDTO>builder()
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
