package com.imchobo.sayren_back.domain.payment.portone.client;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelRequest;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.portone.mapper.PortOneMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class PortOnePaymentClient {
  private final PortOneTokenClient tokenClient;
  private final PortOneMapper  portOneMapper;
  private final RestTemplate restTemplate;

  // 결제 단건 조회
//  public PaymentInfoResponse getPaymentInfo(String impUid) {
//    String token = tokenClient.getAccessToken();
//    String url = "https://api.iamport.kr/payments/" + impUid;
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setBearerAuth(token);
//
//    HttpEntity<Void> entity = new HttpEntity<>(headers);
//    ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
//
//    log.info("PortOne 결제 조회 응답: {}", res.getBody());
//
//    if (res.getBody() == null || res.getBody().get("response") == null) {
//      throw new RuntimeException("PortOne 결제 조회 실패: " + res);
//    }
//    Map<String, Object> response =(Map<String, Object>) res.getBody().get("response");
//    return portOneMapper.toPaymentInfoResponse(response);
//  }
//
//  // 환불 요청
//  public CancelResponse cancelPayment(CancelRequest cancelRequest) {
//    String token = tokenClient.getAccessToken();
//    String url = "https://api.iamport.kr/payments/cancel";
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    headers.setBearerAuth(token);
//
//    HttpEntity<CancelRequest> entity = new HttpEntity<>(cancelRequest, headers);
//    ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);
//
//    log.info("PortOne 환불 응답: {}", res.getBody());
//    if (res.getBody() == null || res.getBody().get("response") == null) {
//      throw  new RuntimeException("PortOne 환불 실패 : " + res);
//    }
//    Map<String, Object> response = (Map<String, Object>) res.getBody().get("response");
//    return portOneMapper.toCancelResponse(response);
//  }
}
