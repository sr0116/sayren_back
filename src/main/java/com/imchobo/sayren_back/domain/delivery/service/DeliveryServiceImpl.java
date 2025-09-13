package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;          // DTO
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;          // 엔티티
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;      // 하위 엔티티
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;    // MapStruct
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository; // 저장소
import lombok.RequiredArgsConstructor;                                   // 생성자 주입
import org.springframework.stereotype.Service;                           // 서비스 컴포넌트
import org.springframework.transaction.annotation.Transactional;         // 트랜잭션

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional                                   // 메서드 전체 트랜잭션
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository; // 저장소 주입
    private final DeliveryMapper deliveryMapper;         // DTO↔Entity 변환

    @Override
    public Long createDelivery(DeliveryDTO dto) {
        // 1) DTO → 엔티티 변환(헤더)
        Delivery delivery = deliveryMapper.toEntity(dto);  // items는 ignore
        if (delivery.getStatus() == null) {                // 상태 기본값 보정
            delivery.setStatus("READY");
        }

        // 2) 배송 아이템(주문아이템 목록) 매핑
        if (dto.getOrderItemIds() != null) {
            for (Long orderItemId : dto.getOrderItemIds()) {
                DeliveryItem item = DeliveryItem.builder()
                        .orderItemId(orderItemId)                 // 숫자 스냅샷만
                        .build();
                delivery.addItem(item);                       // 양방향 연관관계 편의 메서드
            }
        }

        // 3) 저장
        Delivery saved = deliveryRepository.save(delivery);

        // 4) PK 반환
        return saved.getDeliveryId();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDTO getDelivery(Long id) {
        // 1) 조회
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송을 찾을 수 없습니다. id=" + id));

        // 2) 엔티티 → DTO 변환
        DeliveryDTO dto = deliveryMapper.toDTO(delivery);

        // 3) items → orderItemIds로 변환
        if (delivery.getItems() != null) {
            dto.setOrderItemIds(
                    delivery.getItems().stream().map(DeliveryItem::getOrderItemId).toList()
            );
        }
        return dto;
    }

    @Override
    public void updateStatus(Long id, String status, String trackingNo) {
        // 1) 헤더 조회
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송을 찾을 수 없습니다. id=" + id));

        // 2) 상태 변경
        delivery.setStatus(status);

        // 3) 송장번호 입력이 있으면 반영
        if (trackingNo != null && !trackingNo.isBlank()) {
            delivery.setTrackingNo(trackingNo);
        }
        // JPA 변경감지로 자동 flush
    }
}
