package com.imchobo.sayren_back.domain.product.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.product.dto.ProductCreateRequestDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductModifyRequestDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface ProductMapper {
  // 상품 등록 (DTO -> Entity)
  @Mapping(source = "productName", target = "name")
  Product toEntity(ProductCreateRequestDTO dto);

  // 상품 수정
  @Mapping(source = "productId", target = "id")
  @Mapping(source = "productName", target = "name")
  Product toEntity (ProductModifyRequestDTO dto);

  // Entity -> 상품 상세 DTO
  @Mapping(source = "id", target = "productId")
  @Mapping(source = "name", target = "productName")
  ProductDetailsResponseDTO toDetailsDTO(Product product);

  //  Entity -> 상품 목록 DTO
  @Mapping(source = "id", target = "productId")
  @Mapping(source = "name", target = "productName")
  ProductListResponseDTO toListDTO(Product product);




}
