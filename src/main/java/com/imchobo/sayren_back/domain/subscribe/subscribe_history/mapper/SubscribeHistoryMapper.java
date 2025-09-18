package com.imchobo.sayren_back.domain.subscribe.subscribe_history.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.subscribe.subscribe_history.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_history.entity.SubscribeHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface SubscribeHistoryMapper {

  // 엔티티 → 응답 DTO
  @Mapping(source = "id", target = "historyId")
  @Mapping(source = "subscribe.id", target = "subscribeId")
  SubscribeHistoryResponseDTO toResponseDTO(SubscribeHistory entity);

  // 리스트 변환
  List<SubscribeHistoryResponseDTO> toResponseDTOList(List<SubscribeHistory> entities);
}