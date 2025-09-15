package com.imchobo.sayren_back.domain.subscribe_payment.controller;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import com.imchobo.sayren_back.domain.subscribe_payment.service.SubscribePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribe-payments")
@RequiredArgsConstructor
public class SubscribePaymentController {

  private final SubscribePaymentService subscribePaymentService;

  // 특정 구독 회차 전체 조회

}
