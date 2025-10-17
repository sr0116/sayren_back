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

  //  장바구니 및 단일 상품 주문 생성
  @Override
  public OrderResponseDTO createOrder(OrderRequestDTO dto) {
    Member member = SecurityUtil.getMemberEntity();
    Long memberId = member.getId();

    log.info("[장바구니 주문 생성 요청] memberId={}, addressId={}", memberId, dto.getAddressId());

    if (dto.getAddressId() == null) {
      throw new IllegalArgumentException("배송지 정보가 존재하지 않습니다.");
    }

    // 장바구니 확인
    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);

    // 장바구니가 비어 있으면 단일 주문 처리
    if (cartItems == null || cartItems.isEmpty()) {
      log.info("[장바구니 비어 있음 → 단일 주문 처리] memberId={}, productId={}, planId={}",
              memberId, dto.getProductId(), dto.getPlanId());

      Product product = productRepository.findById(dto.getProductId())
              .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + dto.getProductId()));

      OrderPlan plan = orderPlanRepository.findById(dto.getPlanId())
              .orElseThrow(() -> new EntityNotFoundException("요금제를 찾을 수 없습니다. id=" + dto.getPlanId()));

      Address address = addressRepository.findById(dto.getAddressId())
              .orElseThrow(() -> new OrderNotFoundException(dto.getAddressId()));

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

      statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
      eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

      //  첫 번째 OrderItem ID 추출
      Long firstItemId = savedOrder.getOrderItems().get(0).getId();
      log.info("[단일 상품 주문 완료] orderId={}, orderItemId={}", savedOrder.getId(), firstItemId);

      //  OrderResponseDTO에 추가 세팅
      OrderResponseDTO responseDTO = orderMapper.toResponseDTO(savedOrder);
      // responseDTO 안에 orderItemId 필드 추가 (세터 사용)
      // → DTO에 세터 이미 존재하므로 가능
      // 단, orderItems 내부에 값은 이미 포함되어 있음

      //  첫 OrderItemId를 responseDTO 상위에도 세팅
      responseDTO.setOrderItems(responseDTO.getOrderItems());
      responseDTO.setRegDate(savedOrder.getRegDate());
      return responseDTO;
    }

    // 장바구니 기반 주문 처리
    return createOrderFromCart(dto.getAddressId());
  }

  //  장바구니 기반 주문 처리
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
            .collect(Collectors.toList());

    order.setOrderItems(orderItems);
    Order savedOrder = orderRepository.save(order);

    cartRepository.deleteAll(cartItems);

    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    Long firstItemId = savedOrder.getOrderItems().get(0).getId();
    log.info("[장바구니 주문 완료] orderId={}, firstItemId={}", savedOrder.getId(), firstItemId);

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

    // 주문 엔티티 조회
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    // Lazy 로딩된 컬렉션 및 연관 엔티티 강제 초기화
    // - orderItems: 주문에 포함된 상품들
    // - histories: 주문 상태 이력
    // - product, orderPlan: 각 주문상품이 참조하는 상품/요금제 정보
    order.getOrderItems().forEach(item -> {
      if (item.getProduct() != null) {
        item.getProduct().getName(); // 상품 엔티티 로딩
      }
      if (item.getOrderPlan() != null) {
        item.getOrderPlan().getType(); // 요금제 엔티티 로딩
      }
    });
    order.getHistories().size(); // 주문 이력 강제 로드

    // DTO 변환 (Mapper 정상 작동)
    return orderMapper.toResponseDTO(order);
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
