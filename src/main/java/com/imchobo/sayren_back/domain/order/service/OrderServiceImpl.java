package com.imchobo.sayren_back.domain.order.service;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.mapper.OrderMapper;
import com.imchobo.sayren_back.domain.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
//  private final AddressRepository addressRepository;
//  private final CartRepository cartRepository;
  private final OrderMapper orderMapper;

  @Override
  public OrderRequestDTO getOrderById(Long orderId) {
    return null;
  }

  @Override
  public List<OrderRequestDTO> getOrdersByMemberId(Long memberId) {
    return List.of();
  }

  /**
   * 장바구니 → 주문 생성
   */
  @Override
  public OrderRequestDTO createOrderFromCart(Long memberId, Long addressId) {
    // 1. 회원 조회
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new EntityNotFoundException("회원 없음: id=" + memberId));

    // 2. 배송지 조회
//    Address address = addressRepository.findById(addressId)
//      .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + addressId));
//
//    // 3. 장바구니 조회
//    List<CartItem> cartItems = cartRepository.findByMemberId(memberId);
//    if (cartItems.isEmpty()) {
//      throw new IllegalArgumentException("장바구니가 비어있습니다.");
//    }
//
//    // 4. 주문 생성
//    Order order = Order.builder()
//      .member(member)
//      .address(address)
//      .status("PENDING") // 주문 최초 상태
//      .build();
//
//    // 5. 장바구니 아이템 → 주문 아이템 변환
//    List<OrderItem> orderItems = cartItems.stream()
//      .map(cart -> OrderItem.builder()
//        .order(order) // 연관관계 주입
//        .product(cart.getProduct())
//        .plan(cart.getPlan()) // 일반구매면 null
//        .productPriceSnapshot(cart.getProduct().getPrice()) // 현재 가격 snapshot
//        .build()
//      ).collect(Collectors.toList());
//
//    order.setOrderItems(orderItems);
//
//    // 6. 주문 저장 (cascade로 orderItems도 저장됨)
//    Order savedOrder = orderRepository.save(order);
//
//    // 7. 장바구니 비우기
//    cartRepository.deleteAll(cartItems);

    // 8. DTO 변환 후 리턴 (member email/name, address 문자열 포함)
//    return orderMapper.toDTO(savedOrder);
    return null;


  }
}
