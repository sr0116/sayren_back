package com.imchobo.sayren_back.domain.subscribe.subscribe_round.controller;


import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe-payments")
@RequiredArgsConstructor
public class SubscribePaymentController {

  private final SubscribePaymentService subscribePaymentService;

  // 특정 구독 회차 전체 조회

}
