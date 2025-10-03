package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.component.StatusChanger;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import com.imchobo.sayren_back.domain.order.exception.EmptyCartException;
import com.imchobo.sayren_back.domain.order.exception.InvalidOrderStatusException;
import com.imchobo.sayren_back.domain.order.exception.OrderAlreadyCanceledException;
import com.imchobo.sayren_back.domain.order.exception.OrderNotFoundException;
import com.imchobo.sayren_back.domain.order.mapper.OrderMapper;
import com.imchobo.sayren_back.domain.order.cart.repository.CartRepository;
import com.imchobo.sayren_back.domain.order.repository.OrderRepository;
import com.imchobo.sayren_back.domain.order.component.event.OrderPlacedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final AddressRepository addressRepository;
  private final CartRepository cartRepository;
  private final OrderMapper orderMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final StatusChanger statusChanger;

  // 새로 추가된 DTO 기반 메서드
  @Override
  public OrderResponseDTO createOrder(Long memberId, OrderRequestDTO dto) {
    if (dto.getAddressId() != null) {
      // 기존 배송지 사용
      return createOrderFromCart(memberId, dto.getAddressId());
    }

    // 새 배송지 생성
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new OrderNotFoundException(memberId));

    Address newAddress = Address.builder()
      .member(member)
      .name(dto.getReceiverName())
      .tel(dto.getReceiverTel())
      .zipcode(dto.getZipcode())
      .address(dto.getDetail())   //  detail → address 로 매핑
      .isDefault(false)           // 기본배송지 여부 (필요시 true)
      .memo(dto.getMemo())
      .build();


    Address savedAddress = addressRepository.save(newAddress);

    return createOrderFromCart(memberId, savedAddress.getId());
  }

  @Override
  public OrderResponseDTO createOrderFromCart(Long memberId, Long addressId) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new OrderNotFoundException(memberId));

    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new EntityNotFoundException("주소 없음: " + addressId));

    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
    if (cartItems.isEmpty()) {
      throw new EmptyCartException(memberId);
    }

    Order order = Order.builder()
      .member(member)
      .address(address)
      .status(OrderStatus.PENDING)
      .build();

    List<OrderItem> orderItems = cartItems.stream()
      .map(cart -> OrderItem.builder()
        .order(order)
        .product(cart.getProduct())
        .orderPlan(cart.getOrderPlan())
        .productPriceSnapshot(cart.getProduct().getPrice())
        .build()
      ).collect(Collectors.toList());

    // 엔티티 저장
    Order savedOrder = orderRepository.save(order);

    // 장바구니 비우기
    cartRepository.deleteAll(cartItems);

    // 이벤트 발행 (배송 자동 생성)
    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    // 상태 기록
    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);

    return orderMapper.toResponseDTO(savedOrder);
  }

  @Override
  public OrderResponseDTO markAsPaid(Long orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() != OrderStatus.PENDING) {
      throw new InvalidOrderStatusException("결제 대기 상태의 주문만 결제 완료로 변경 가능");
    }

    statusChanger.change(order, OrderStatus.PAID, ActorType.SYSTEM);
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }

  @Override
  public OrderResponseDTO cancel(Long orderId, String reason) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() == OrderStatus.CANCELED) {
      throw new OrderAlreadyCanceledException(orderId);
    }

    statusChanger.change(order, OrderStatus.CANCELED, ActorType.USER);
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(Long orderId) {
    return orderMapper.toResponseDTO(orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByMemberId(Long memberId) {
    return orderRepository.findByMemberIdOrderByIdDesc(memberId)
      .stream()
      .map(orderMapper::toResponseDTO)
      .collect(Collectors.toList());
  }
}