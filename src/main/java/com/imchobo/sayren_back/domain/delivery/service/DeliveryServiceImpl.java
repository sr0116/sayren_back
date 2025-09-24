package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.delivery.sharedevent.DeliveryStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기

    @Override
    public DeliveryResponseDTO create(DeliveryRequestDTO dto) {
        Delivery entity = deliveryMapper.toEntity(dto);
        // 초기 상태 기본값 (READY)
        if (entity.getStatus() == null) entity.setStatus(DeliveryStatus.READY);
        Delivery saved = deliveryRepository.save(entity);

        publishEvent(saved); // 생성 시에도 이벤트 발행 가능
        return deliveryMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO get(Long id) {
        Delivery d = mustFind(id);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByMember(Long memberId) {
        return deliveryRepository.findByMember_Id(memberId)
          .stream()
          .map(deliveryMapper::toResponseDTO)
          .toList();
    }

    // ── 상태 전환 ────────────────────────────────

    @Override
    public DeliveryResponseDTO prepare(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.READY, "READY 에서만 PREPARING으로 전환 가능");
        d.setStatus(DeliveryStatus.PREPARING);

        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO ship(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.PREPARING, "PREPARING 에서만 SHIPPING으로 전환 가능");
        d.setStatus(DeliveryStatus.SHIPPING);

        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO complete(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.SHIPPING, "SHIPPING 에서만 DELIVERED로 전환 가능");
        d.setStatus(DeliveryStatus.DELIVERED);

        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO pickupReady(Long id) {
        Delivery d = mustFind(id);
        d.setStatus(DeliveryStatus.PICKUP_READY);

        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO pickedUp(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.PICKUP_READY, "PICKUP_READY 에서만 PICKED_UP으로 전환 가능");
        d.setStatus(DeliveryStatus.PICKED_UP);

        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    // ── helpers ────────────────────────────────
    private Delivery mustFind(Long id) {
        return deliveryRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다. id=" + id));
    }

    private void ensure(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }

    // ── 이벤트 발행 ─────────────────────────────
    private void publishEvent(Delivery delivery) {
        eventPublisher.publishEvent(
          new DeliveryStatusChangedEvent(
            delivery.getId(),
            delivery.getStatus(),
            delivery.getType(),
            delivery.getMember().getId()
          )
        );
    }
}
