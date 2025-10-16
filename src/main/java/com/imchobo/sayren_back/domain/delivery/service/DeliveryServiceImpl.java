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

    //  결제 성공 시 주문아이템 기반 배송 생성 (자동)
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

    //  상태 변경 (READY → SHIPPING → DELIVERED → RETURNED)
    @Override
    public void changedStatus(DeliveryStatusChangeDTO dto) {
        Delivery delivery = mustFind(dto.getDeliveryId());
        OrderItem orderItem = delivery.getDeliveryItems().get(0).getOrderItem();

        deliveryStatusChanger.changeDeliveryStatus(
          delivery,
          delivery.getType(),
          dto.getStatus(),
          orderItem
        );

        log.info("[배송 상태 변경 완료] deliveryId={}, next={}", dto.getDeliveryId(), dto.getStatus());
    }

    //  단건 조회
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO get(Long id) {
        return deliveryMapper.toResponseDTO(mustFind(id));
    }

    //  회원별 조회
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByMember(Long memberId) {
        return deliveryRepository.findByMember_Id(memberId)
          .stream().map(deliveryMapper::toResponseDTO).toList();
    }

    //  주문 기준 조회
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getByOrder(Long orderId) {
        return deliveryRepository.findByDeliveryItems_OrderItem_Order_Id(orderId)
          .stream().map(deliveryMapper::toResponseDTO).toList();
    }

    //  전체 조회 (어드민)
    @Override
    public PageResponseDTO<DeliveryResponseDTO, Delivery> getAllList(PageRequestDTO pageRequestDTO) {
        Page<Delivery> result = deliveryRepository.findAll(pageRequestDTO.getPageable());
        return PageResponseDTO.of(result, deliveryMapper::toResponseDTO);
    }

    // Helper
    private Delivery mustFind(Long id) {
        return deliveryRepository.findById(id)
          .orElseThrow(() -> new DeliveryNotFoundException(id));
    }
}
