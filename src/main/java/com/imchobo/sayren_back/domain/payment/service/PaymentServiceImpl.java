package com.imchobo.sayren_back.domain.payment.service;


import com.imchobo.sayren_back.domain.common.config.ApiResponse;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.portone.client.PortOnePaymentClient;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.portone.dto.CancelRequest;
import com.imchobo.sayren_back.domain.payment.portone.dto.CancelResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  // PortOne api 호출 및 연동
  private final PortOnePaymentClient portOnePaymentClient;


  // 결제 준비
  @Transactional
  @Override
  public ApiResponse<PaymentResponseDTO> prepare(PaymentRequestDTO dto) {
    Payment payment = paymentMapper.toEntity(dto);
    // merchant_uid 생성
    String merchantUid = "pay_" + UUID.randomUUID().toString().replace("-", "");
    payment.setMerchantUid(merchantUid);
    paymentRepository.save(payment);
    return ApiResponse.ok(paymentMapper.toResponseDTO(payment));
  }

  // 결제 완료 처리
  @Override
  public ApiResponse<PaymentResponseDTO> complete(Long paymentId, String impUid) {
    // paymentId 조회
    Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("결제 아이디를 찾을 수 없습니다."));

    // 포트원 결제 정보 조회
    PaymentInfoResponse paymentInfo = portOnePaymentClient.getPaymentInfo(impUid);
    log.info("PortOne 결제 정보: {}", paymentInfo);

    PaymentStatus mappedStatus = PaymentStatus.fromPortOneStatus(paymentInfo.getStatus());
    // 결제 상태 (만약 paid 상태 아니면 미완료 상태로 두고 )
    if (mappedStatus != PaymentStatus.PAID) {
      payment.setPayStatus(mappedStatus);
      paymentRepository.save(payment);
      throw new RuntimeException("결제 미완료 상태 :" + paymentInfo.getStatus());
    }
// 가격 일치 여부
    if (!paymentInfo.getAmount().equals(payment.getAmount())) {
      log.info("결제 금액 불일치: 요청 금액={}, PortOne 금액 ={}", payment.getAmount(), paymentInfo.getAmount());
      return ApiResponse.fail("결제 금액이 일치하지 않습니다.");
    }

    // DB 저장 (성공시)
    payment.setImpUid(impUid);
    payment.setPayStatus(PaymentStatus.PAID); // 상태 변경(결제) - 이미 dto에서 기본값 줘서 변경만 해주면 됨
    paymentRepository.save(payment);

    // DTO
    PaymentResponseDTO paymentResponseDTO = paymentMapper.toResponseDTO(payment);
    log.info("결제 완료 처리: {}", payment);

    return ApiResponse.ok(paymentResponseDTO);
  }

  // 환불 처리
  @Override
  @Transactional
  public ApiResponse<Void> refund(Long paymentId, Long amount, String reason) {

    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("결제 아이디를 찾을 수 없습니다."));
// 환불 요청
    CancelRequest cancelRequest = new CancelRequest(
            payment.getImpUid(),
            payment.getMerchantUid(),
            reason,
            amount
    );
    // api 호출
    CancelResponse cancelResponse = portOnePaymentClient.cancelPayment(cancelRequest);
// DB 저장
    payment.setPayStatus(PaymentStatus.REFUNDED);
    paymentRepository.save(payment);

    return ApiResponse.ok(null);
  }

//  조회 리스트 (JPA)


  @Override
  public  ApiResponse<List<PaymentResponseDTO>> getAll() {
    List<PaymentResponseDTO> list = paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "regdate"))
            .stream()
            .map(paymentMapper::toResponseDTO)
            .toList();
    return ApiResponse.ok(list);
  }
}
