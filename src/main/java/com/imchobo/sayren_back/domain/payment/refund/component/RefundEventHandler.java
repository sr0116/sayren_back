package com.imchobo.sayren_back.domain.payment.refund.component;


import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
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
  private final RefundRequestRepository refundRequestRepository;

  // 관리자가 환불 승인 시에 호출됨

  @EventListener
  @Transactional
  public void onRefundApproved(RefundApprovedEvent event) {
    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

    refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
            .ifPresentOrElse(req -> {
              if (req.getStatus() == RefundRequestStatus.PENDING) {
                req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
                req.setReasonCode(event.getReason()); // 승인 사유 기록
                refundRequestRepository.save(req); // 명시적 저장
                log.info("환불 요청 상태 변경 완료: refundRequestId={}, 상태=PENDING→APPROVED_WAITING_RETURN", req.getId());
              } else {
                log.warn("환불 요청 상태 변경 불가: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
              }
            }, () -> log.warn("해당 구독에 대한 환불 요청 없음: subscribeId={}", event.getSubscribeId()));
  }


  // 회수 완료 되었을 때 환불 실행
  @EventListener
  @Transactional
  public void onDeliveryReturned(SubscribeStatusChangedEvent event) {
    if (event.getTransition() == SubscribeTransition.RETURNED_AND_CANCELED) {
      Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
              .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
              .ifPresent(req -> {
                if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
                  try {
                    refundService.executeRefundForSubscribe(subscribe, event.getTransition().getReason());

                    // 환불 완료 상태 반영 (이전은 아직 회수전 상태)
                    req.setStatus(RefundRequestStatus.APPROVED);
                    refundRequestRepository.save(req);

                    log.info("회수 완료 → 환불 성공: refundRequestId={}, 상태=APPROVED_WAITING_RETURN→APPROVED",
                            req.getId());
                  } catch (Exception e) {
                    log.error("PortOne 환불 실패: refundRequestId={}, message={}", req.getId(), e.getMessage());
                    // 선택: req.setStatus(RefundRequestStatus.FAILED);
                  }
                } else {
                  log.warn("회수 완료 이벤트 무시: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
                }
              });
    }
  }
}

