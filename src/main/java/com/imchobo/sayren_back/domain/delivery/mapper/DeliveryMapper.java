package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface DeliveryMapper {

    // Spring 주입을 쓰므로 INSTANCE는 굳이 안 써도 됨.
    // 필요하면 남겨둬도 무방하지만 보통은 주입으로 받음.
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    // RequestDTO → Entity
    // dto.orderId( Long ) → entity.order( Order )
    @Mapping(target = "order", source = "orderId", qualifiedByName = "mapOrder")
    // 주소 등 단순 필드는 이름 같으면 자동 매핑됨
    Delivery toEntity(DeliveryRequestDTO dto);

    // Entity → ResponseDTO
    // entity.order( Order ) → dto.orderId( Long )
    @Mapping(target = "orderId", source = "order", qualifiedByName = "mapOrderId")
    // 상태 enum → 문자열로 내보내려면 아래처럼 표현식으로 처리 (필요 시)
    @Mapping(target = "status",
      expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    DeliveryResponseDTO toResponseDTO(Delivery entity);
}
