package com.imchobo.sayren_back.domain.delivery.en;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Getter;

/**
 * 배송 상태 전환 + 실패/취소/예외 사유 Enum
 * - PaymentTransition 과 동일한 스타일
 */
@Getter
public enum DeliveryTransition {

  CREATE_READY(DeliveryStatus.READY, ReasonCode.NONE),                 // 배송 생성됨 (결제 완료 직후)
  SHIP(DeliveryStatus.SHIPPING, ReasonCode.NONE),                      // 배송 시작
  COMPLETE(DeliveryStatus.DELIVERED, ReasonCode.NONE),                 // 배송 완료
  RETURN_REQUEST(DeliveryStatus.RETURN_READY, ReasonCode.USER_REQUEST),// 고객 반납 요청
  RETURN_PROCESS(DeliveryStatus.IN_RETURNING, ReasonCode.NONE),        // 회수 진행
  RETURNED(DeliveryStatus.RETURNED, ReasonCode.AUTO_REFUND),           // 회수 완료 + 자동 환불
  FAIL_SYSTEM(DeliveryStatus.RETURN_READY, ReasonCode.SYSTEM_ERROR);   // 시스템 장애

  private final DeliveryStatus status;
  private final ReasonCode reason;

  DeliveryTransition(DeliveryStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }

  /**
   * 외부에서 문자열 기반 이벤트가 들어올 때
   *
   * 예: "ship", "complete" 같은 문자열이 메시지큐, Kafka, API 요청 바디 등으로 들어오고
   * 이걸 바로 DeliveryTransition으로 바꿔야 할 때.
   *
   * 이런 경우는 fromEvent 같은 매핑 메서드가 있어야 문자열 → Enum 변환이 가능해.
   */
  public static DeliveryTransition fromEvent(String eventName) {
    switch (eventName.toLowerCase()) {
      case "create": return CREATE_READY;
      case "ship": return SHIP;
      case "complete": return COMPLETE;
      case "return_request": return RETURN_REQUEST;
      case "return_process": return RETURN_PROCESS;
      case "returned": return RETURNED;
      default: return FAIL_SYSTEM;
    }
  }
}