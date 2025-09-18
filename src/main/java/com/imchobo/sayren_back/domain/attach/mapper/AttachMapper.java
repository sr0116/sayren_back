package com.imchobo.sayren_back.domain.attach.mapper;

import com.imchobo.sayren_back.domain.attach.dto.AttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface AttachMapper {
  // Entity -> DTO
  @Mapping(source="id", target = "attachId")  // Attach.id -> DTO.id
  @Mapping(source = ".", target = "url", qualifiedByName = "mapAttachUrl") // Attach -> URL
  AttachResponseDTO toDTO(Attach attach);

}
