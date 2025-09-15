package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateRequest;
import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateResponse;
import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO;

import java.util.List;

public interface AddressService {
//  Long create(AddressDTO dto);                        // addr-01 등록
//  void setDefault(Long memberId, Long addrId);        // addr-02 기본설정
//  void update(AddressDTO dto);                        // 수정
//  void delete(Long memberId, Long addrId);            // addr-03 삭제(승격 포함)
//  List<AddressDTO> list(Long memberId);               // 회원별 목록
//  AddressDTO get(Long memberId, Long addrId);         // 단건 조회


  // 주소 등록
  AddressCreateResponse create(AddressCreateRequest request);

  // 주소 단건 조회
  AddressDTO getById(Long addrId);

  // 특정 회원의 모든 주소 조회
  List<AddressDTO> getByMemberId(Long memberId);

  // 기본 배송지 설정
  void setDefault(Long memberId, Long addrId);

  // 주소 수정
  void update(AddressDTO dto);

  // 주소 삭제
  void delete(Long memberId, Long addrId);
}
