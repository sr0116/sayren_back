package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.component.StatusChanger;
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
  private final OrderRepository orderRepository;      // 주문 저장/조회
  private final MemberRepository memberRepository;    // 회원 조회
  private final AddressRepository addressRepository;  // 주소 조회
  private final CartRepository cartRepository;        // 장바구니 조회/삭제
  private final OrderMapper orderMapper;              // Entity ↔ DTO 변환기
  private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기
  private final StatusChanger statusChanger;          // 상태 전환 + 히스토리 기록


  // 장바구니 → 주문 생성 (PENDING 상태)
  @Override
  public OrderResponseDTO createOrderFromCart(Long memberId, Long addressId) {
    // 1. 회원 조회 (없으면 예외)
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new OrderNotFoundException(memberId));

    // 2. 주소 조회 (없으면 예외)
    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new OrderNotFoundException(addressId));

    // 3. 장바구니 조회 (비어있으면 예외)
    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
    if (cartItems.isEmpty()) {
      throw new EmptyCartException(memberId);
    }

    // 4. 주문 엔티티 생성 (기본 상태 = PENDING)
    Order order = Order.builder()
      .member(member)
      .address(address)
      .status(OrderStatus.PENDING)
      .build();

    // 5. 장바구니 아이템 → 주문 아이템으로 변환
    List<OrderItem> orderItems = cartItems.stream()
      .map(cart -> OrderItem.builder()
        .order(order)
        .product(cart.getProduct())
        .orderPlan(cart.getOrderPlan())
        .productPriceSnapshot(cart.getProduct().getPrice())
        .build()
      ).collect(Collectors.toList());

    // TODO: Order 엔티티에 setOrderItems() 있으면 주석 해제
    // order.setOrderItems(orderItems);

    // 6. 주문 저장
    Order savedOrder = orderRepository.save(order);

    // 7. 장바구니 비우기
    cartRepository.deleteAll(cartItems);

    // 8. 주문 생성 이벤트 발행 → 배송 자동 생성

    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    // 9. 상태 기록 (PENDING, actor = USER)
    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);

    // 10. DTO 변환 후 반환
    return orderMapper.toResponseDTO(savedOrder);
  }

  // 결제 성공 → 주문 상태 PAID
  @Override
  public OrderResponseDTO markAsPaid(Long orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() != OrderStatus.PENDING) {
      throw new InvalidOrderStatusException("결제 대기 상태의 주문만 결제 완료로 변경할 수 있습니다.");
    }

    statusChanger.change(order, OrderStatus.PAID, ActorType.SYSTEM);

    Order updated = orderRepository.save(order);
    return orderMapper.toResponseDTO(updated);
  }

  // 결제 실패/취소 → 주문 상태 CANCELED
  @Override
  public OrderResponseDTO cancel(Long orderId, String reason) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() == OrderStatus.CANCELED) {
      throw new OrderAlreadyCanceledException(orderId);
    }

    statusChanger.change(order, OrderStatus.CANCELED, ActorType.USER);

    Order updated = orderRepository.save(order);
    return orderMapper.toResponseDTO(updated);
  }

  // 단일 주문 조회
  @Override
  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));
    return orderMapper.toResponseDTO(order);
  }

  // 회원별 주문 목록 조회
  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByMemberId(Long memberId) {
    List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
    return orders.stream()
      .map(orderMapper::toResponseDTO)
      .collect(Collectors.toList());
  }
}