package com.imchobo.sayren_back.domain.delivery.mapper; // 맵퍼 패키지

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO; // DTO
import com.imchobo.sayren_back.domain.delivery.entity.Delivery; // 엔티티
import org.mapstruct.Mapper;           // MapStruct 인터페이스
import org.mapstruct.Mapping;          // 필드 매핑 규칙 지정

@Mapper(componentModel = "spring")     // 스프링 빈으로 등록
public interface DeliveryMapper {

    // DTO -> Entity 변환 시 items 컬렉션은 서비스에서 따로 구성하므로 무시
    @Mapping(target = "items", ignore = true)
    Delivery toEntity(DeliveryDTO dto);

    // Entity -> DTO (items는 서비스에서 orderItemIds로 풀어 세팅)
    DeliveryDTO toDTO(Delivery entity);
}
