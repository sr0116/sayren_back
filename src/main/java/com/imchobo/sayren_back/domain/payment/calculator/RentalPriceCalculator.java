package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.subscribe.dto.RentalPriceDTO;
import org.springframework.stereotype.Component;

@Component
public class RentalPriceCalculator implements PriceCalculator{
  // 월 렌탈료 계산 로직

  // 렌탈가 인상률 (상품가 * 1.05 = 5% 인상)
  private static final long PRICE_INCREASE_RATE = 105;
  // 보증금 비율 (상품가 대비 20%)
  private static final long DEPOSIT_RATE = 20;

  public RentalPriceDTO calculate(Long productPrice, int months) {
    if (productPrice <= 0 || months <= 0) {
      throw new IllegalArgumentException("상품 가격과 개월 수는 0보다 커야 합니다.");
    }
    // 상품가격
    long adjustedPrice = (productPrice * PRICE_INCREASE_RATE) / 100;

    // 월 렌탈료
    long monthlyFee = adjustedPrice / months;
    // 보증금
    long deposit = (productPrice * DEPOSIT_RATE) / 100;

    // 총 렌탈가
    monthlyFee = floorToTenWon(monthlyFee);
    deposit = floorToTenWon(deposit);
    long totalPrice = floorToTenWon((monthlyFee * months) + deposit);

    return RentalPriceDTO.builder()
            .monthlyFee(monthlyFee)
            .deposit(deposit)
            .totalPrice(totalPrice)
            .build();
  }

  // 10원 단위 내림 (버림)
  private long floorToTenWon(long value) {
    return (value / 10) * 10;
  }
}