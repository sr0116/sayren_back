package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.delivery.component.DeliveryStatusChanger;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.dto.admin.DeliveryStatusChangeDTO;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.exception.*;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Log4j2
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
    private final DeliveryStatusChanger deliveryStatusChanger;


    // 배송 생성 (사용자 직접)

    @Override
    public DeliveryResponseDTO create(DeliveryRequestDTO dto) {
//        Member member = SecurityUtil.getMemberEntity();
//        log.info(" 배송 생성 요청 → memberId={}, addressId={}", member.getId(), dto.getAddressId());
//
//        Address address = addressRepository.findById(dto.getAddressId())
//          .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + dto.getAddressId()));
//
//        if (!address.getMember().getId().equals(member.getId())) {
//            throw new DeliveryUnauthorizedException();
//        }
//
//        Delivery delivery = Delivery.builder()
//          .member(member)
//          .address(address)
//          .type(DeliveryType.DELIVERY)
//          .status(DeliveryStatus.READY)
//          .build();
//
//        Delivery saved = deliveryRepository.save(delivery);
//        log.info("배송 생성 완료 → deliveryId={}, memberId={}", saved.getId(), member.getId());
//
//        return deliveryMapper.toResponseDTO(saved);
    return null;
    }


    // 전체 목록 (어드민)

    @Override
    public PageResponseDTO<DeliveryResponseDTO, Delivery> getAllList(PageRequestDTO pageRequestDTO) {
        Page<Delivery> result = deliveryRepository.findAll(pageRequestDTO.getPageable());
        return PageResponseDTO.of(result, deliveryMapper::toResponseDTO);
    }


    // 주문아이템 기반 배송 생성 (결제 성공 시 자동)

    @Override
    public void createFromOrderItemId(Long orderItemId) {
//        OrderItem orderItem = orderItemRepository.findById(orderItemId)
//          .orElseThrow(() -> new DeliveryNotFoundException(orderItemId));
////
//        if (deliveryRepository.existsByDeliveryItems_OrderItem_Id(orderItemId)) {
//            throw new DeliveryAlreadyExistsException(orderItem.getOrder().getId());
//        }
//
//        Order order = orderItem.getOrder();
//
//        Delivery delivery = Delivery.builder()
//          .member(order.getMember())
//          .address(order.getAddress())
//          .type(DeliveryType.DELIVERY)
//          .status(DeliveryStatus.READY)
//          .build();
//
//        Delivery saved = deliveryRepository.save(delivery);
//
//        DeliveryItem deliveryItem = DeliveryItem.builder()
//          .delivery(saved)
//          .orderItem(orderItem)
//          .build();
//        deliveryItemRepository.save(deliveryItem);
//
//        log.info(" 주문아이템 기반 배송 생성 완료 → orderItemId={}, deliveryId={}", orderItemId, saved.getId());
    }


    // 조회

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


    //  상태 전환

    @Override
    public void changeStatus(DeliveryStatusChangeDTO dto) {
        Long deliveryId = dto.getDeliveryId();
        switch (dto.getStatus()) {
            case READY -> ship(deliveryId);
            case SHIPPING -> complete(deliveryId);
            case DELIVERED -> returnReady(deliveryId);
            case RETURN_READY -> inReturning(deliveryId);
            case IN_RETURNING -> returned(deliveryId);
            default -> throw new DeliveryStatusInvalidException("잘못된 배송 상태 요청: " + dto.getStatus());
        }
    }

    @Override
    public void changedStatus(DeliveryStatusChangeDTO dto) {
        Delivery delivery = mustFind(dto.getDeliveryId());
        OrderItem orderItem = delivery.getDeliveryItems().get(0).getOrderItem();

        switch (dto.getStatus()) {
            case SHIPPING -> deliveryStatusChanger.changeDeliveryStatus(delivery, delivery.getType(), DeliveryStatus.SHIPPING, orderItem);
            case DELIVERED -> deliveryStatusChanger.changeDeliveryStatus(delivery, delivery.getType(), DeliveryStatus.DELIVERED, orderItem);
            case RETURNED -> deliveryStatusChanger.changeDeliveryStatus(delivery, DeliveryType.RETURN, DeliveryStatus.RETURNED, orderItem);
            default -> throw new DeliveryStatusInvalidException("잘못된 상태 전환 요청: " + dto.getStatus());
        }
    }

    @Override
    public DeliveryResponseDTO ship(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.READY, "READY → SHIPPING만 가능");
        log.info(" 배송 시작 → deliveryId={}", id);

        flow.changeStatus(d, DeliveryStatus.READY, DeliveryStatus.SHIPPING, Map.of("source", "DeliveryService#ship"));
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO complete(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.SHIPPING, "SHIPPING → DELIVERED만 가능");
        log.info(" 배송 완료 → deliveryId={}", id);

        flow.changeStatus(d, DeliveryStatus.SHIPPING, DeliveryStatus.DELIVERED, Map.of("source", "DeliveryService#complete"));
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO returnReady(Long id) {
        Delivery d = mustFind(id);
        d.setType(DeliveryType.RETURN);
        ensure(d.getStatus() == DeliveryStatus.DELIVERED, "DELIVERED → RETURN_READY만 가능");
        log.info(" 회수 준비 → deliveryId={}", id);

        flow.changeStatus(d, DeliveryStatus.DELIVERED, DeliveryStatus.RETURN_READY, Map.of("source", "DeliveryService#returnReady"));
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO inReturning(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.RETURN_READY, "RETURN_READY → IN_RETURNING만 가능");
        log.info("♻ 회수 중 → deliveryId={}", id);

        flow.changeStatus(d, DeliveryStatus.RETURN_READY, DeliveryStatus.IN_RETURNING, Map.of("source", "DeliveryService#inReturning"));
        return deliveryMapper.toResponseDTO(d);
    }

    @Override
    public DeliveryResponseDTO returned(Long id) {
        Delivery d = mustFind(id);
        ensure(d.getStatus() == DeliveryStatus.IN_RETURNING, "IN_RETURNING → RETURNED만 가능");
        log.info(" 회수 완료 → deliveryId={}", id);

        flow.changeStatus(d, DeliveryStatus.IN_RETURNING, DeliveryStatus.RETURNED, Map.of("source", "DeliveryService#returned"));
        return deliveryMapper.toResponseDTO(d);
    }


    //  Helper Methods

    private Delivery mustFind(Long id) {
        return deliveryRepository.findById(id)
          .orElseThrow(() -> new DeliveryNotFoundException(id));
    }

    private void ensure(boolean cond, String msg) {
        if (!cond) throw new DeliveryStatusInvalidException(msg);
    }
}
