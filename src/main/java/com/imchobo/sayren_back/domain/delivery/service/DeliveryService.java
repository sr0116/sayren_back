package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import java.util.List;

public interface DeliveryService {
    DeliveryResponseDTO create(DeliveryRequestDTO dto); // 수동 생성(API)
    DeliveryResponseDTO get(Long id);
    List<DeliveryResponseDTO> getByMember(Long memberId);

    // 상태 전환 (엔티티 최종본 상태셋에 맞춤)
    DeliveryResponseDTO prepare(Long id);     // READY -> PREPARING
    DeliveryResponseDTO ship(Long id);        // PREPARING -> SHIPPING
    DeliveryResponseDTO complete(Long id);    // SHIPPING -> DELIVERED
    DeliveryResponseDTO pickupReady(Long id); // RETURN 타입 회수 준비
    DeliveryResponseDTO pickedUp(Long id);    // 회수 완료
}
