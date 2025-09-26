package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.delivery.service.processor.DeliveryFlowOrchestrator;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final DeliveryMapper deliveryMapper;

    private final DeliveryFlowOrchestrator flow;

    /**
     * 사용자 직접 생성 (테스트/예외 케이스용)
     */
    @Override
    public DeliveryResponseDTO create(DeliveryRequestDTO dto) {
        Member currentMember = SecurityUtil.getMemberEntity();

        Address address = addressRepository.findById(dto.getAddressId())
          .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + dto.getAddressId()));

        if (!address.getMember().getId().equals(currentMember.getId())) {
            throw new IllegalStateException("본인 주소만 사용할 수 있습니다.");
        }

        Delivery delivery = Delivery.builder()
          .member(currentMember)
          .address(address)
          .type(DeliveryType.DELIVERY)
          .status(DeliveryStatus.READY)
          .build();

        Delivery saved = deliveryRepository.save(delivery);

        // 초기 READY 이벤트 발행 (expected=null)
        flow.changeStatus(
          saved,
          null,
          DeliveryStatus.READY,
          Map.of("source", "DeliveryService#create")
        );

        return deliveryMapper.toResponseDTO(saved);
    }

    /**
     * ✅ 결제 성공 직후 orderItemId 기반 배송 자동 생성
     */
    @Override
    public void createFromOrderItemId(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
          .orElseThrow(() -> new EntityNotFoundException("OrderItem 없음: id=" + orderItemId));

        // 이미 배송이 생성된 OrderItem이면 중복 생성 방지
        if (deliveryRepository.existsByDeliveryItems_OrderItem_Id(orderItemId)) return;


        Order order = orderItem.getOrder();

        Delivery delivery = Delivery.builder()
          .member(order.getMember())
          .address(order.getAddress())
          .type(DeliveryType.DELIVERY)
          .status(DeliveryStatus.READY)
          .build();

        Delivery saved = deliveryRepository.save(delivery);

        // DeliveryItem 매핑
        DeliveryItem deliveryItem = DeliveryItem.builder()
          .delivery(saved)
          .orderItem(orderItem)
          .build();
        deliveryItemRepository.save(deliveryItem);

        // READY 이벤트 발행
        flow.changeStatus(
          saved,
          null,
          DeliveryStatus.READY,
          Map.of("source", "DeliveryService#createFromOrderItemId",
            "orderItemId", orderItemId)
        );
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
          .stream().map(deliveryMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByOrder(Long orderId) {
        return deliveryRepository.findByDeliveryItems_OrderItem_Order_Id(orderId)
          .stream().map(deliveryMapper::toResponseDTO).toList();
    }

    // ===== 상태 전환 =====
    @Override
    public DeliveryResponseDTO ship(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.READY, "READY → SHIPPING만 가능");

        flow.changeStatus(
          d,
          DeliveryStatus.READY,
          DeliveryStatus.SHIPPING,
          Map.of("source", "DeliveryService#ship")
        );

        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO complete(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.SHIPPING, "SHIPPING → DELIVERED만 가능");

        flow.changeStatus(
          d,
          DeliveryStatus.SHIPPING,
          DeliveryStatus.DELIVERED,
          Map.of("source", "DeliveryService#complete")
        );

        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO returnReady(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.DELIVERED, "DELIVERED → RETURN_READY만 가능");

        flow.changeStatus(
          d,
          DeliveryStatus.DELIVERED,
          DeliveryStatus.RETURN_READY,
          Map.of("source", "DeliveryService#returnReady")
        );

        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO inReturning(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.RETURN_READY, "RETURN_READY → IN_RETURNING만 가능");

        flow.changeStatus(
          d,
          DeliveryStatus.RETURN_READY,
          DeliveryStatus.IN_RETURNING,
          Map.of("source", "DeliveryService#inReturning")
        );

        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO returned(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.IN_RETURNING, "IN_RETURNING → RETURNED만 가능");

        flow.changeStatus(
          d,
          DeliveryStatus.IN_RETURNING,
          DeliveryStatus.RETURNED,
          Map.of("source", "DeliveryService#returned")
        );

        return deliveryMapper.toResponseDTO(d);
    }

    // ===== helpers =====
    private Delivery mustFind(Long id) {
        return deliveryRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다. id=" + id));
    }

    private void ensure(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }
}