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
import com.imchobo.sayren_back.domain.product.service.ProductService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
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
    private final ProductService productService;

    /**
     * 상품을 게시글(Board)로 등록
     */
    @Override
    public void registerProductBoard(ProductCreateRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(EntityNotFoundException::new);

        // 게시글 생성
        Board board = Board.builder()
                .product(Product.builder().id(dto.getProductId()).build())
                .title(null)
                .content(null)
                .category(Category.builder().id(dto.getCategoryId()).build())
                .isSecret(false)
                .member(SecurityUtil.getMemberEntity())
                .status(CommonStatus.ACTIVE)
                .build();

        //  게시글 저장
        boardRepository.save(board);

        //  상품 상태 변경 (노출중 처리)
        product.setIsUse(true);
        productRepository.save(product);

        productService.revalidate(dto.getProductId());
        productService.revalidateAll();
    }

//    /**
//     * 상품을 게시글(Board)로 삭제
//     */
//    @Override
//    public void deleteProductBoard(ProductCreateRequestDTO dto) {
//        Product product = productRepository.findById(dto.getProductId()).orElseThrow(EntityNotFoundException::new);
//
//        // 게시글 생성
//        Board board = Board.builder()
//                .product(Product.builder().id(dto.getProductId()).build())
//                .title(null)
//                .content(null)
//                .category(Category.builder().id(dto.getCategoryId()).build())
//                .isSecret(false)
//                .member(SecurityUtil.getMemberEntity())
//                .status(CommonStatus.DISABLED)
//                .build();
//
//        //  게시글 저장
//        boardRepository.save(board);
//
//        //  상품 상태 변경 (노출중 처리)
//        product.setIsUse(true);
//        productRepository.save(product);
//
//        productService.revalidate(dto.getProductId());
//        productService.revalidateAll();
//    }
}
