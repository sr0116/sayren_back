package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.common.en.CommonStatus;
import com.imchobo.sayren_back.domain.product.dto.*;
import com.imchobo.sayren_back.domain.product.entity.Product;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
//  void preloadProducts();

  List<ProductListResponseDTO> getAllProducts();
  ProductDetailsResponseDTO getProductById(Long id);

  // 승인 완료(활성화)페이지네이션
  PageResponseDTO<ProductPendingDTO, Product> getPendingProducts(PageRequestDTO pageRequestDTO);
  // 삭제(비활성화) 페이지네이션
  PageResponseDTO<ProductPendingDTO, Product> getApprovedProducts(PageRequestDTO pageRequestDTO);

  // 단순 전체 조회
//  List<ProductPendingDTO> getPendingProducts();
//  List<ProductPendingDTO> getApprovedProducts();

  // 승인 / 취소
  void useProduct(Long id);
  void cancelUseProduct(Long id);

  // 등록대기 상세
  ProductDetailsResponseDTO getProductDetailForAdmin(Long id);

  // 상품 수정
  void modifyProduct(Long id, ProductModifyRequestDTO productModifyRequestDTO);

  void revalidate(Long id);
  void revalidateAll();

  // 상품 큐레이션
  Page<ProductListResponseDTO> getFilteredProducts(ProductListResponseDTO filter, Pageable pageable);

//  Long registerProduct(ProductCreateRequestDTO dto, Long memberId);
  // 상품을 게시글로 등록
  Long registerProduct(ProductCreateRequestDTO dto);

  void registerProductBoard(ProductCreateRequestDTO dto);

  Object getProductCategories();

  // 관리자 상품 관리 페이지
  List<ProductListResponseDTO> getAllProductsForAdmin();
}

