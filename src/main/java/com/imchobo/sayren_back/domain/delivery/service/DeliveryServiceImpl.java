package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryResponseDTO getDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("배송 정보가 존재하지 않습니다."));
        return deliveryMapper.toResponseDTO(delivery); // 인스턴스 메서드 호출
    }

    @Override
    public List<DeliveryResponseDTO> getDeliveriesByMember(Long memberId) {
        List<Delivery> deliveries = deliveryRepository.findByMemberId(memberId);
        return deliveries.stream()
          .map(deliveryMapper::toResponseDTO) //  인스턴스 방식으로 사용
          .toList();
    }
}
