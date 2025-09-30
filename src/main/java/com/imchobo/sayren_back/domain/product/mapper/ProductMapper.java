package com.imchobo.sayren_back.domain.product.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductCreateRequestDTO;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductModifyRequestDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface ProductMapper {
  // 상품 등록 (DTO -> Entity)
  @Mapping(source = "productName", target = "name")
  Product toEntity(PurchaseProductCreateRequestDTO dto);

  // 상품 수정
  @Mapping(source = "productId", target = "id")
  @Mapping(source = "productName", target = "name")
  Product toEntity (PurchaseProductModifyRequestDTO dto);

  // Entity -> 상품 상세 DTO
  @Mapping(source = "id", target = "productId")
  @Mapping(source = "name", target = "productName")
  PurchaseProductDetailsResponseDTO toDetailsDTO(Product product);

  //  Entity -> 상품 목록 DTO
  @Mapping(source = "id", target = "productId")
  @Mapping(source = "name", target = "productName")
  PurchaseProductListResponseDTO toListDTO(Product product);




}
