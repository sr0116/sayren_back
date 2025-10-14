package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.dto.admin.DeliveryStatusChangeDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;

import java.util.List;

public interface DeliveryService {

    // ── 기본 CRUD ────────────────────────────────
    DeliveryResponseDTO create(DeliveryRequestDTO dto);

    DeliveryResponseDTO get(Long id);

    List<DeliveryResponseDTO> getByMember(Long memberId);

    // 주문 ID 기준 조회
    List<DeliveryResponseDTO> getByOrder(Long orderId);

    // 전체조회(어드민)
    PageResponseDTO<DeliveryResponseDTO, Delivery> getAllList(PageRequestDTO pageRequestDTO);

    // ── 상태 전환 ────────────────────────────────
    void changeStatus(DeliveryStatusChangeDTO deliveryStatusChangeDTO);
    void changedStatus(DeliveryStatusChangeDTO dto);

    // 배송 시작 (READY → SHIPPING)
    DeliveryResponseDTO ship(Long id);

    // 배송 완료 (SHIPPING → DELIVERED)
    DeliveryResponseDTO complete(Long id);

    // 회수 준비 (DELIVERED → RETURN_READY)
    DeliveryResponseDTO returnReady(Long id);

    // 회수 중 (RETURN_READY → IN_RETURNING)
    DeliveryResponseDTO inReturning(Long id);

    // 회수 완료 (IN_RETURNING → RETURNED)
    DeliveryResponseDTO returned(Long id);

    // ----------------------------------
    void createFromOrderItemId(Long orderItemId);// 결제 성공 직후 자동 생성
}
