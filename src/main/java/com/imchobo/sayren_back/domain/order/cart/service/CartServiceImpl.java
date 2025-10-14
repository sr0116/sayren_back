package com.imchobo.sayren_back.domain.order.cart.service;

import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.cart.exception.CartAlreadyExistsException;
import com.imchobo.sayren_back.domain.order.cart.exception.CartNotFoundException;
import com.imchobo.sayren_back.domain.order.cart.mapper.CartItemMapper;
import com.imchobo.sayren_back.domain.order.cart.repository.CartRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.OrderPlan.repository.OrderPlanRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final OrderPlanRepository orderPlanRepository;
  private final CartItemMapper cartItemMapper;

  @Override
  public void addItem(CartItemAddRequestDTO requestDTO) {

    // 장바구니 저장
    cartRepository.save(
      CartItem.builder()
        .member(SecurityUtil.getMemberEntity())
        .product(Product.builder().id(requestDTO.getProductId()).build())
        .orderPlan(OrderPlan.builder().id(requestDTO.getOrderPlanId()).build())
        .build()
    );
  }

  // 특정 회원의 장바구니 조회
  @Override
  public List<CartItemResponseDTO> getCartItems() {
    List<CartItem> cartItemList = cartRepository.findByMemberId(SecurityUtil.getMemberAuthDTO().getId());
    return cartItemList.stream().map(cartItemMapper::toResponseDTO).toList();
  }

  @Override
  public void removeItem(Long cartItemId) {
    cartRepository.deleteById(cartItemId);
  }

  @Override
  public void clearCart() {
    cartRepository.deleteByMemberId(SecurityUtil.getMemberAuthDTO().getId());
  }
}