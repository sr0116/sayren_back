package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class SubscribePaymentService {

//  @Transactional
//  public PaymentResponseDTO prepareForRound(SubscribeRound round) {
//    Member member = round.getSubscribe().getMember();
//    OrderItem orderItem = round.getSubscribe().getOrderItem();
//
//    String merchantUid = "pay_" + UUID.randomUUID().toString().replace("-", "");
//    if (paymentRepository.findByMerchantUid(merchantUid).isPresent()) {
//      throw new PaymentAlreadyExistsException(merchantUid);
//    }
//
//    Payment payment = new Payment();
//    payment.setMember(member);
//    payment.setOrderItem(orderItem);
//    payment.setSubscribeRound(round);
//    payment.setMerchantUid(merchantUid);
//    payment.setAmount(round.getAmount());
//
//    Payment saved = paymentRepository.saveAndFlush(payment);
//    paymentHistoryRecorder.recordInitPayment(saved);
//
//    return paymentMapper.toResponseDTO(saved);
//  }
}
