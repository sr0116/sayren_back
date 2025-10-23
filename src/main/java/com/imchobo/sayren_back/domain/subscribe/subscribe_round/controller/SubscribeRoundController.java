package com.imchobo.sayren_back.domain.subscribe.subscribe_round.controller;


import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribeRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/subscribe_round")
@RequiredArgsConstructor
public class SubscribeRoundController {

  private final SubscribeRoundService subscribeRoundService;


}
