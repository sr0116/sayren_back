package com.imchobo.sayren_back.servicetest;

import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateRequest;
import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateResponse;
import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO;
import com.imchobo.sayren_back.domain.delivery.service.AddressService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AddressServiceIntegrationTest {

  @Autowired
  private AddressService addressService;

  private static Long savedAddrId;

  /**
   * 주소 등록 테스트
   */
  @Test
  @Order(1)
  void 주소등록() {
    AddressCreateRequest request = AddressCreateRequest.builder()
      .memberId(1L)
      .name("홍길동")
      .tel("010-1111-2222")
      .zipcode("12345")
      .address("서울시 강남구 테스트동")
      .defaultAddress(true)
      .memo("통합 테스트 메모")
      .build();

    AddressCreateResponse response = addressService.create(request);

    assertThat(response).isNotNull();
    assertThat(response.getAddrId()).isNotNull();

    savedAddrId = response.getAddrId(); // PK 저장
    System.out.println(" 등록된 주소 ID: " + savedAddrId);
  }

  /**
   * 주소 단건 조회 테스트
   */
  @Test
  @Order(2)
  void 주소단건조회() {
    assertThat(savedAddrId).isNotNull(); // 방어코드 추가

    AddressDTO dto = addressService.getById(savedAddrId);

    assertThat(dto).isNotNull();
    assertThat(dto.getName()).isEqualTo("홍길동");
    assertThat(dto.getTel()).isEqualTo("010-1111-2222");
    assertThat(dto.getZipcode()).isEqualTo("12345");

    System.out.println(" 단건 조회된 주소: " + dto);
  }

  /**
   * 회원별 주소 목록 조회 테스트
   */
  @Test
  @Order(3)
  void 회원별주소조회() {
    List<AddressDTO> list = addressService.getByMemberId(1L);

    assertThat(list).isNotEmpty();
    assertThat(list.size()).isGreaterThan(0);

    list.forEach(dto -> System.out.println("조회된 주소: " + dto));
  }
}
