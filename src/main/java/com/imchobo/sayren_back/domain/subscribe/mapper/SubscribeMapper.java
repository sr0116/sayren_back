package com.imchobo.sayren_back.domain.subscribe.mapper;

import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscribeMapper {

  // 클라이언트 요청 DTO
  //클라이언트가 구독 신청을 보낼 때, RequestDTO를 받아서 DB에 저장할 Entity로 변환
  Subscribe toEntity(SubscribeRequestDTO dto);

  // 응답
  // DB에서 조회한 Subscribe Entity를 클라이언트 응답용 DTO로 변환
  SubscribeResponseDTO toResponseDTO(Subscribe entity);

  // 마이페이지 같은 곳에서 간단 현황만 보여줄 때 사용
  SubscribeSummaryDTO toSummaryDTO(Subscribe entity);

  // 엔티티 리스트를 받아서 DTO 리스트로 변환
  List<SubscribeResponseDTO> toResponseDTOList(List<Subscribe> entity);
  // 엔티티 리스트를 받아서 구독 요약 보여줄 때 사용
  List<SubscribeSummaryDTO> toSummaryDTOList(List<Subscribe> entities);
}
