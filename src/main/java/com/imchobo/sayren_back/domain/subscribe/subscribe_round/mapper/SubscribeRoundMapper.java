package com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto.SubscribeRoundRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto.SubscribeRoundResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class},  imports = {LocalDateTime.class})
public interface SubscribeRoundMapper {
  // 디티오를 엔티티
  @Mapping(target = "id", ignore = true)   // PK 자동 생성
  @Mapping(source = "subscribeId", target = "subscribe", qualifiedByName = "mapSubscribe")
  @Mapping(target = "payStatus", ignore = true)   // 기본값 PENDING (@Builder.Default)
  @Mapping(target = "paidDate", ignore = true)
  SubscribeRound toEntity(SubscribeRoundRequestDTO dto);

  // 엔티티를 디티오

  @Mapping(source = "id", target = "subscribeRoundId")
  @Mapping(source = "subscribe.id", target = "subscribeId")
  SubscribeRoundResponseDTO toDto(SubscribeRound entity);
  // 리스트 조회시 필요
  List<SubscribeRoundResponseDTO> toResponseDTOS(List<SubscribeRound> entities);

}
