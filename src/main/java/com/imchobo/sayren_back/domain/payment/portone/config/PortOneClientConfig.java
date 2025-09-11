package com.imchobo.sayren_back.domain.payment.portone.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * PortOneClientConfig
 * - application.yml에 정의된 포트원 API 키/시크릿/상점코드 값을 로드하는 설정 클래스
 * - ServiceImpl에서 이 설정을 주입받아 API 호출 시 사용
 */
@Configuration
@Getter
@Log4j2
public class PortOneClientConfig {


   // PortOne REST API Key
  @Value("${portone.api-key}")
  private String apiKey;


//  PortOne secret API Key
  @Value("${portone.api-secret}")
  private String apiSecret;


   //  상점 코드 (merchant code, MID)
  @Value("${portone.merchant-code}")
  private String merchantCode;

  //  콘솔에서 값 들어가 있는지 확인용 (나중에 지워도 됨)
  @PostConstruct
  public void init() {
    log.info("PortOne 설정 확인 → apiKey={}, apiSecret={}, merchantCode={}",
            apiKey, apiSecret, merchantCode);


  }


}