package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.delivery.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.CartItem;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import com.imchobo.sayren_back.domain.order.mapper.OrderMapper;
import com.imchobo.sayren_back.domain.order.repository.OrderRepository;
import com.imchobo.sayren_back.domain.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.cart.repository.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
//  private final AddressRepository addressRepository;
//  private final CartRepository cartRepository;
  private final OrderMapper orderMapper;

  /**
   * 장바구니 → 주문 생성
   */
  @Override
  public OrderResponseDTO createOrderFromCart(Long memberId, Long addressId) {
    // 1. 회원 조회
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new EntityNotFoundException("회원 없음: id=" + memberId));

    // 2. 배송지 조회
//    Address address = addressRepository.findById(addressId)
//      .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + addressId));

    // 3. 장바구니 조회
//    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
//    if (cartItems.isEmpty()) {
//      throw new IllegalArgumentException("장바구니가 비어있습니다.");
//    }

    // 4. 주문 생성
    Order order = Order.builder()
//      .member(member)
//      .address(address)
//      .status(OrderStatus.PENDING) // enum 사용
      .build();

    // 5. 장바구니 아이템 → 주문 아이템 변환
//    List<OrderItem> orderItems = cartItems.stream()
//      .map(cart -> OrderItem.builder()
//        .order(order) //  여기서 바로 연관관계 주입
//        .product(cart.getProduct())
//        .orderPlan(cart.getPlan()) // 일반구매면 null
//        .productPriceSnapshot(cart.getProduct().getPrice()) // snapshot
//        .build()
//      ).collect(Collectors.toList());



    // 6. 주문 저장 (cascade로 orderItems도 저장됨)
    Order savedOrder = orderRepository.save(order);

    // 7. 장바구니 비우기
//    cartRepository.deleteAll(cartItems);

    // 8. DTO 변환 후 반환
    return orderMapper.toResponseDTO(savedOrder);
  }

  /**
   * 단일 주문 조회
   */
  @Override
  public OrderResponseDTO getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
      .orElseThrow(() -> new EntityNotFoundException("주문 없음: id=" + orderId));
    return orderMapper.toResponseDTO(order);
  }

  /**
   * 회원별 주문 목록 조회
   */
  @Override
  public List<OrderResponseDTO> getOrdersByMemberId(Long memberId) {
    List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
    return orders.stream()
      .map(orderMapper::toResponseDTO)
      .collect(Collectors.toList());
  }
}
