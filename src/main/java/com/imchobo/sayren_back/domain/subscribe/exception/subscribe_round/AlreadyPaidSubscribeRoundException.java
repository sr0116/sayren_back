package com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class AlreadyPaidSubscribeRoundException extends SayrenException {

  public AlreadyPaidSubscribeRoundException(Long roundId) {
    super("ALREADY_PAID_SUBSCRIBE_ROUND",
            "이미 결제된 회차입니다. roundId=" + roundId);
  }

}
