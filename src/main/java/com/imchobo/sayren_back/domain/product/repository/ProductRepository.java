package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


  // 단순 카테고리 조회
  List<Product> findByProductCategory(String productCategory);

  // orderPlan 타입만 조건
  @Query("SELECT DISTINCT p FROM Product p " +
          "JOIN p.orderItems oi " +
          "JOIN oi.orderPlan op " +
          "WHERE op.type = :type")
  List<Product> findByOrderPlanType(@Param("type") OrderPlanType type);

  // 카테고리 + 타입 조건
  @Query("SELECT DISTINCT p FROM Product p " +
          "JOIN p.orderItems oi " +
          "JOIN oi.orderPlan op " +
          "WHERE p.productCategory = :category " +
          "AND op.type = :type")
  List<Product> findByCategoryAndType(@Param("category") String category,
                                      @Param("type") OrderPlanType type);

  // 등록 승인대기 상품 목록
  List<Product> findByIsUseFalse();
  // 등록 승인 완료 목록
  List<Product> findByIsUseTrue();

  // 등록 승인대기 상품 목록 페이징 조회
  Page<Product> findByIsUseFalse(Pageable pageable);

  // 등록 승인 완료 상품 페이지 조회
  Page<Product> findByIsUseTrue(Pageable pageable);


  // 큐레이션 필터
  @Query("""
          SELECT DISTINCT p
          FROM Product p
          WHERE p.isUse = true
          AND (:#{#keyword} IS NULL OR p.name LIKE %:#{#keyword}%)
          AND (:#{#category} IS NULL OR p.productCategory = :#{#category})
          ORDER BY p.regDate DESC
        """)
  Page<Product> searchByFilter(
          @Param("keyword") String keyword,
          @Param("category") String category,
          @Param("sort") String sort,
          Pageable pageable
  );
}
