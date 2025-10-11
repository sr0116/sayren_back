package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
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

  // 등록 승인 목록
  List<Product> findByIsUseTrue();
}
