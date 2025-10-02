package com.imchobo.sayren_back.domain.payment.refund.component;


import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class RefundEventHandler {

  private final RefundService refundService;
  private final SubscribeRepository subscribeRepository;
  private final ApplicationEventPublisher eventPublisher;

  // 관리자가 환불 승인 시에 호출됨

  @EventListener
  @Transactional
  public void onRefundApproved(RefundApprovedEvent event) {
    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

    // 상태는 ACTIVE 유지, RefundRequest.status 만 APPROVED_WAITING_RETURN 으로 변경
    log.info("환불 승인됨(회수 대기 중): subscribeId={}, reason={}", event.getSubscribeId(), event.getReason());
  }

  // 회수 완료 되었을 때 환불 실행

  @EventListener
  @Transactional
  public void onDeliveryReturned(SubscribeStatusChangedEvent event) {
    // SubscribeStatusChangedEvent 중 RETURNED_AND_CANCELED 인 경우에만 환불 실행
    if (event.getTransition() == SubscribeTransition.RETURNED_AND_CANCELED) {
      Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
              .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

      // 실제 환불 실행
      refundService.executeRefundForSubscribe(subscribe, event.getTransition().getReason());

      log.info("회수 완료 이벤트 → 환불 실행 완료: subscribeId={}", event.getSubscribeId());
    }
  }
}
