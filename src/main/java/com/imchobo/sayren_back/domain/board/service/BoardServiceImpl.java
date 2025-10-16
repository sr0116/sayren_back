package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.common.en.CommonStatus;
import com.imchobo.sayren_back.domain.product.dto.ProductCreateRequestDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;

    /**
     * 상품을 게시글(Board)로 등록
     */
    @Override
    public void registerProductBoard(ProductCreateRequestDTO dto) {

        // 상품 조회
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        //  카테고리 조회
        Category category = categoryRepository
                .findByNameAndParentCategory_Type(dto.getProductCategory(), CategoryType.PRODUCT)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
        // 게시글 생성
        Board board = Board.builder()
                .product(product)
                .title(product.getName())
                .content(product.getDescription())
                .category(category)
                .isSecret(false)
                .status(CommonStatus.ACTIVE)
                .build();

        //  게시글 저장
        boardRepository.save(board);

        //  상품 상태 변경 (노출중 처리)
        product.setIsUse(true);
        productRepository.save(product);
    }
}
