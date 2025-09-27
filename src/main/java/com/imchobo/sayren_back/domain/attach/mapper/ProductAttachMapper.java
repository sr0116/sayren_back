package com.imchobo.sayren_back.domain.attach.mapper;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface ProductAttachMapper {
  // Entity -> 상품 DTO
  @Mapping(source = "id", target = "attachId")
  @Mapping(source = ".", target = "url", qualifiedByName = "mapAttachUrl")
  // Attach -> URL
  ProductAttachResponseDTO toProductDTO(Attach attach);

}