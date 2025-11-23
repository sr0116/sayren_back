package com.imchobo.sayren_back.domain.payment.en;

import java.util.Locale;

public enum PaymentType {
  KAKAO, TOSS, CARD;

  public static PaymentType fromPortOne(String pgProvider) {
    if (pgProvider == null) return CARD; // 기본값 CARD

    return switch (pgProvider.toLowerCase()) {
      case "kakaopay", "kakao" -> KAKAO;
      case "tosspayments", "toss" -> TOSS;
      default -> CARD;
    };
  }
}
