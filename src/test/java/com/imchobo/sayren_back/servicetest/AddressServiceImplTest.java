package com.imchobo.sayren_back.servicetest;
import com.imchobo.sayren_back.domain.address.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback(false)   // DB에 값이 남도록 설정
class AddressServiceImplTest {

  @Autowired
  private AddressService addressService;

  @Test
  void 주소_등록과_조회() {
//    AddressDTO dto = AddressDTO.builder()
//      .memberId(1L)
//      .name("홍길동")
//      .tel("010-1234-5678")
//      .zipcode("12345")
//      .address("서울시 강남구 어딘가 101호")
//      .defaultAddress(true)
//      .memo("문 앞에 놓아주세요")
//      .build();
//
//    Long addrId = addressService.create(dto);
//
//    AddressDTO saved = addressService.get(1L, addrId);
//    assertThat(saved.getName()).isEqualTo("홍길동");
//    assertThat(saved.getDefaultAddress()).isTrue();
  }



  @Test
  void 기본배송지_하나만_보장() {
//    AddressDTO dto1 = AddressDTO.builder()
//      .memberId(1L)
//      .name("김철수")
//      .tel("010-1111-2222")
//      .zipcode("54321")
//      .address("부산시 해운대구 11호")
//      .defaultAddress(true)
//      .build();
//
//    AddressDTO dto2 = AddressDTO.builder()
//      .memberId(2L)
//      .name("김영희")
//      .tel("010-3333-4444")
//      .zipcode("67890")
//      .address("부산시 해운대구 22호")
//      .defaultAddress(true)
//      .build();
//
//    Long id1 = addressService.create(dto1);
//    Long id2 = addressService.create(dto2);
//
//    AddressDTO addr1 = addressService.get(2L, id1);
//    AddressDTO addr2 = addressService.get(2L, id2);
//
//    assertThat(addr1.getDefaultAddress()).isFalse();
//    assertThat(addr2.getDefaultAddress()).isTrue();
  }

  @Test
  void 주소_수정() {
//    AddressDTO dto = AddressDTO.builder()
//      .memberId(3L)
//      .name("박영희")
//      .tel("010-5555-6666")
//      .zipcode("11111")
//      .address("대구시 동구 테스트동")
//      .defaultAddress(false)
//      .build();
//
//    Long addrId = addressService.create(dto);
//
//    AddressDTO updateDto = AddressDTO.builder()
//      .addrId(addrId)
//      .memberId(3L)
//      .name("박영희2")
//      .tel("010-7777-8888")
//      .zipcode("22222")
//      .address("대구시 서구 수정동")
//      .defaultAddress(true)
//      .memo("부재시 경비실")
//      .build();
//
//    addressService.update(updateDto);
//
//    AddressDTO updated = addressService.get(3L, addrId);
//    assertThat(updated.getName()).isEqualTo("박영희2");
//    assertThat(updated.getDefaultAddress()).isTrue();
  }

  @Test
  void 주소_삭제와_기본주소_승격() {
//    AddressDTO dto1 = AddressDTO.builder()
//      .memberId(4L)
//      .name("테스트1")
//      .tel("010-9999-0000")
//      .zipcode("33333")
//      .address("광주시 북구 1호")
//      .defaultAddress(true)
//      .build();
//
//    AddressDTO dto2 = AddressDTO.builder()
//      .memberId(4L)
//      .name("테스트2")
//      .tel("010-1111-0000")
//      .zipcode("44444")
//      .address("광주시 북구 2호")
//      .defaultAddress(false)
//      .build();
//
//    Long id1 = addressService.create(dto1);
//    Long id2 = addressService.create(dto2);
//
//    addressService.delete(4L, id1);
//
//    AddressDTO addr2 = addressService.get(4L, id2);
//    assertThat(addr2.getDefaultAddress()).isTrue();
  }

  @Test
  void 다른회원은_수정삭제_불가() {
//    AddressDTO dto = AddressDTO.builder()
//      .memberId(5L)
//      .name("회원A")
//      .tel("010-2222-3333")
//      .zipcode("55555")
//      .address("울산시 남구")
//      .defaultAddress(true)
//      .build();
//
//    Long addrId = addressService.create(dto);
//
//    assertThrows(IllegalArgumentException.class,
//      () -> addressService.delete(999L, addrId));
  }
}
