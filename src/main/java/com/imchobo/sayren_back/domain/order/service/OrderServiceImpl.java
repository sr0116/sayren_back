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

  /** 장바구니 기반 주문 생성 */
  @Override
  public OrderResponseDTO createOrder(OrderRequestDTO dto) {
    Member member = SecurityUtil.getMemberEntity();
    Long memberId = member.getId();

    log.info("[장바구니 주문 생성] memberId={}", memberId);

    Long addressId = dto.getAddressId() != null
      ? dto.getAddressId()
      : addressRepository.save(Address.builder()
      .member(member)
      .name(dto.getReceiverName())
      .tel(dto.getReceiverTel())
      .zipcode(dto.getZipcode())
      .address(dto.getDetail())
      .isDefault(false)
      .memo(dto.getMemo())
      .build()).getId();

    return createOrderFromCart(addressId);
  }

  /** 장바구니 기반 주문 처리 */
  @Override
  public OrderResponseDTO createOrderFromCart(Long addressId) {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    Member member = SecurityUtil.getMemberEntity();

    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new OrderNotFoundException(addressId));

    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
    if (cartItems.isEmpty()) throw new EmptyCartException(memberId);

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

    cartRepository.deleteAll(cartItems);
    statusChanger.change(savedOrder, OrderStatus.PENDING, ActorType.USER);
    eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId()));

    return orderMapper.toResponseDTO(savedOrder);
  }

  /** 바로구매 주문 생성 */
  @Override
  public OrderResponseDTO createDirectOrder(DirectOrderRequestDTO dto) {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    Member member = SecurityUtil.getMemberEntity();

    log.info("[바로구매 주문 생성] memberId={}, productId={}, planId={}",
      memberId, dto.getProductId(), dto.getPlanId());

    Product product = productRepository.findById(dto.getProductId())
      .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + dto.getProductId()));

    OrderPlan plan = orderPlanRepository.findById(dto.getPlanId())
      .orElseThrow(() -> new EntityNotFoundException("요금제 없음: " + dto.getPlanId()));

    Address address = addressRepository.save(Address.builder()
      .member(member)
      .name(dto.getReceiverName())
      .tel(dto.getReceiverTel())
      .zipcode(dto.getZipcode())
      .address(dto.getDetail())
      .memo(dto.getMemo())
      .isDefault(false)
      .build());

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

    return orderMapper.toResponseDTO(savedOrder);
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(Long orderId) {
    return orderMapper.toResponseDTO(orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByMemberId() {
    Long memberId = SecurityUtil.getMemberAuthDTO().getId();
    return orderRepository.findByMemberIdOrderByIdDesc(memberId)
      .stream()
      .map(orderMapper::toResponseDTO)
      .collect(Collectors.toList());
  }

  @Override
  public OrderResponseDTO markAsPaid(Long orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    statusChanger.change(order, OrderStatus.PAID, ActorType.SYSTEM);
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }

  @Override
  public OrderResponseDTO cancel(Long orderId, String reason) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    statusChanger.change(order, OrderStatus.CANCELED, ActorType.USER);
    return orderMapper.toResponseDTO(orderRepository.save(order));
  }
}
