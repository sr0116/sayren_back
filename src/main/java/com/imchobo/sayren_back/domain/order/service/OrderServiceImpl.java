package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.cart.repository.CartRepository;
import com.imchobo.sayren_back.domain.order.dto.DirectOrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.exception.*;
import com.imchobo.sayren_back.domain.order.mapper.OrderMapper;
import com.imchobo.sayren_back.domain.order.repository.OrderRepository;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.OrderPlan.repository.OrderPlanRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import com.imchobo.sayren_back.domain.order.component.StatusChanger;
import com.imchobo.sayren_back.domain.order.component.event.OrderPlacedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final AddressRepository addressRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final OrderPlanRepository orderPlanRepository;
  private final OrderMapper orderMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final StatusChanger statusChanger;

  //  장바구니 기반 주문 생성
  @Override
  public OrderResponseDTO createOrder(OrderRequestDTO dto) {
    Member member = SecurityUtil.getMemberEntity();
    Long memberId = member.getId();

    log.info("[장바구니 주문 생성 요청] memberId={}, addressId={}", memberId, dto.getAddressId());

    if (dto.getAddressId() == null) {
      throw new IllegalArgumentException("배송지 정보가 존재하지 않습니다.");
    }

    //  장바구니 확인
    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);

    //  장바구니가 비어 있으면 바로 단일 상품 주문 생성
    if (cartItems == null || cartItems.isEmpty()) {
      log.info("[장바구니 비어 있음 → 단일 상품 주문으로 전환] memberId={}, productId={}, planId={}",
        memberId, dto.getProductId(), dto.getPlanId());

      // 바로 구매 로직처럼 처리
      Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + dto.getProductId()));

      OrderPlan plan = orderPlanRepository.findById(dto.getPlanId())
        .orElseThrow(() -> new EntityNotFoundException("요금제를 찾을 수 없습니다. id=" + dto.getPlanId()));

      Address address = addressRepository.findById(dto.getAddressId())
        .orElseThrow(() -> new OrderNotFoundException(dto.getAddressId()));

      // 주문 생성
      Order order = Order.builder()
        .member(member)
        .address(address)
        .status(OrderStatus.PENDING)
        .build();

      OrderItem orderItem = OrderItem.builder()
        .order(order)
        .product(product)
        .orderPlan(plan)
        .productPriceSnapshot(product.getPrice())
        .build();

      order.setOrderItems(List.of(orderItem));
      Order savedOrder = orderRepository.save(order);

      // 상태 변경 및 이벤트 발행
      statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
      eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

      log.info("[단일 상품 주문 완료] orderId={}, memberId={}", savedOrder.getId(), memberId);
      return orderMapper.toResponseDTO(savedOrder);
    }

    //  장바구니 주문 정상 처리
    return createOrderFromCart(dto.getAddressId());
  }

  // 장바구니 기반 주문 처리
  @Override
  public OrderResponseDTO createOrderFromCart(Long addressId) {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    Member member = SecurityUtil.getMemberEntity();

    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new OrderNotFoundException(addressId));

    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
    if (cartItems.isEmpty()) throw new EmptyCartException(memberId);

    log.info("[장바구니 주문 생성 시작] memberId={}, cartItemCount={}", memberId, cartItems.size());

    Order order = Order.builder()
      .member(member)
      .address(address)
      .status(OrderStatus.PENDING)
      .build();

    List<OrderItem> orderItems = cartItems.stream()
      .map(c -> OrderItem.builder()
        .order(order)
        .product(c.getProduct())
        .orderPlan(c.getOrderPlan())
        .productPriceSnapshot(c.getProduct().getPrice())
        .build())
      .toList();

    order.setOrderItems(orderItems);
    Order savedOrder = orderRepository.save(order);

    // 장바구니 비우기
    cartRepository.deleteAll(cartItems);

    // 상태 변경 및 이벤트 발행
    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    log.info("[장바구니 주문 완료] orderId={}, totalItems={}", savedOrder.getId(), orderItems.size());
    return orderMapper.toResponseDTO(savedOrder);
  }

  // 바로구매 주문 생성
  @Override
  public OrderResponseDTO createDirectOrder(DirectOrderRequestDTO dto) {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    Member member = SecurityUtil.getMemberEntity();

    log.info("[바로구매 주문 요청] memberId={}, productId={}, planId={}", memberId, dto.getProductId(), dto.getPlanId());

    Product product = productRepository.findById(dto.getProductId())
      .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + dto.getProductId()));

    OrderPlan plan = orderPlanRepository.findById(dto.getPlanId())
      .orElseThrow(() -> new EntityNotFoundException("요금제를 찾을 수 없습니다. id=" + dto.getPlanId()));

    // 배송지 저장
    Address address = addressRepository.save(Address.builder()
      .member(Member.builder()
        .id(SecurityUtil.getMemberAuthDTO().getId())
        .build())
      .name(dto.getReceiverName())
      .tel(dto.getReceiverTel())
      .zipcode(dto.getZipcode())
      .address(dto.getDetail())
      .memo(dto.getMemo())
      .isDefault(false)
      .build());

    log.info("[배송지 저장 완료] memberId={}, addressId={}", memberId, address.getId());

    // 주문 생성
    Order order = Order.builder()
      .member(member)
      .address(address)
      .status(OrderStatus.PENDING)
      .build();

    OrderItem orderItem = OrderItem.builder()
      .order(order)
      .product(product)
      .orderPlan(plan)
      .productPriceSnapshot(product.getPrice())
      .build();

    order.setOrderItems(List.of(orderItem));
    Order savedOrder = orderRepository.save(order);

    // 상태 전이 + 이벤트
    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    log.info("[바로구매 주문 완료] orderId={}, memberId={}", savedOrder.getId(), memberId);
    return orderMapper.toResponseDTO(savedOrder);
  }

  // 주문 단건 조회
  @Override
  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(Long orderId) {
    log.info("[주문 상세 조회] orderId={}", orderId);
    return orderMapper.toResponseDTO(orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId)));
  }

  // 회원별 주문 목록 조회
  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByMemberId() {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    log.info("[회원 주문 목록 조회] memberId={}", memberId);

    return orderRepository.findByMemberIdOrderByIdDesc(memberId)
      .stream()
      .map(orderMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  // 결제 완료 처리
  @Override
  public OrderResponseDTO markAsPaid(Long orderId) {
    log.info("[결제 완료 처리] orderId={}", orderId);
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    statusChanger.change(order, OrderStatus.PAID, ActorType.SYSTEM);
    eventPublisher.publishEvent(new OrderPlacedEvent(order.getId())); // 결제 이벤트 발행
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }

  // 주문 취소 처리
  @Override
  public OrderResponseDTO cancel(Long orderId, String reason) {
    log.info("[주문 취소 요청] orderId={}, reason={}", orderId, reason);
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    statusChanger.change(order, OrderStatus.CANCELED, ActorType.USER);
    eventPublisher.publishEvent(new OrderPlacedEvent(order.getId())); // 취소 이벤트 발행
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }
}
