package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.dto.admin.DeliveryStatusChangeDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;

import java.util.List;

public interface DeliveryService {

    // 기본 CRUD
    DeliveryResponseDTO create(DeliveryRequestDTO dto);

    DeliveryResponseDTO get(Long id);

    List<DeliveryResponseDTO> getByMember(Long memberId);

    List<DeliveryResponseDTO> getByOrder(Long orderId);

    PageResponseDTO<DeliveryResponseDTO, Delivery> getAllList(PageRequestDTO pageRequestDTO);

    // 상태 전환
    void changedStatus(DeliveryStatusChangeDTO dto);

    //  자동 생성 (결제 성공 시)
    void createFromOrderItemId(Long orderItemId);
}
