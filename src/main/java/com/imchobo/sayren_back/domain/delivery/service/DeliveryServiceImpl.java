package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;
import com.imchobo.sayren_back.domain.delivery.repository.AddressRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.delivery.sharedevent.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
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
    private final AddressRepository addressRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public DeliveryResponseDTO create(DeliveryRequestDTO dto) {
        // 1) 로그인 사용자
        Member currentMember = SecurityUtil.getMemberEntity();

        // 2) 주소 조회 + 소유자 검증
        Address address = addressRepository.findById(dto.getAddressId())
          .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + dto.getAddressId()));

        if (!address.getMember().getId().equals(currentMember.getId())) {
            throw new IllegalStateException("본인 주소만 사용할 수 있습니다.");
        }

        // 3) 항상 DELIVERY 타입으로 생성(반품은 상태로 처리)
        Delivery delivery = Delivery.builder()
          .member(currentMember)
          .address(address)
          .type(DeliveryType.DELIVERY)
          .status(DeliveryStatus.READY)
          .build();

        Delivery saved = deliveryRepository.save(delivery);

        // 필요 시 생성 시점에도 이벤트 발행
        publishEvent(saved);
        return deliveryMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO get(Long id) {
        return deliveryMapper.toResponseDTO(mustFind(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByMember(Long memberId) {
        return deliveryRepository.findByMember_Id(memberId)
          .stream()
          .map(deliveryMapper::toResponseDTO)
          .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByOrder(Long orderId) {
        return deliveryRepository.findByDeliveryItems_OrderItem_Order_Id(orderId)
          .stream()
          .map(deliveryMapper::toResponseDTO)
          .toList();
    }

    // ── 상태 전환 ────────────────────────────────

    @Override
    public DeliveryResponseDTO ship(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.READY, "READY → SHIPPING만 가능");
        d.setStatus(DeliveryStatus.SHIPPING);
        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO complete(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.SHIPPING, "SHIPPING → DELIVERED만 가능");
        d.setStatus(DeliveryStatus.DELIVERED);
        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO returnReady(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.DELIVERED, "DELIVERED → RETURN_READY만 가능");
        d.setStatus(DeliveryStatus.RETURN_READY);
        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO inReturning(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.RETURN_READY, "RETURN_READY → IN_RETURNING만 가능");
        d.setStatus(DeliveryStatus.IN_RETURNING);
        publishEvent(d);
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO returned(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.IN_RETURNING, "IN_RETURNING → RETURNED만 가능");
        d.setStatus(DeliveryStatus.RETURNED);
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
    // -------이벤트발행
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
