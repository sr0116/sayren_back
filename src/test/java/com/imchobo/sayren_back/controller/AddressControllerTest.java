package com.imchobo.sayren_back.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper om;

  @Test
  void 등록_목록_기본설정_삭제_시나리오() throws Exception {
    // 1) 등록 A(default=true)
    AddressDTO a = AddressDTO.builder()
      .memberId(1001L).name("A").tel("010-1").zipcode("00001").address("A")
      .defaultAddress(true).memo("A").build();

    String aId = mockMvc.perform(post("/api/addresses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(a)))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();

    // 2) 등록 B(default=false)
    AddressDTO b = AddressDTO.builder()
      .memberId(1001L).name("B").tel("010-2").zipcode("00002").address("B")
      .defaultAddress(false).memo("B").build();

    String bId = mockMvc.perform(post("/api/addresses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(b)))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();

    // 3) B를 기본으로 전환
    mockMvc.perform(put("/api/addresses/{id}/default", Long.valueOf(bId))
        .param("memberId", "1001"))
      .andExpect(status().isOk());

    // 4) B 삭제 → A 자동 승격
    mockMvc.perform(delete("/api/addresses/{id}", Long.valueOf(bId))
        .param("memberId", "1001"))
      .andExpect(status().isOk());

    // 5) 목록 확인
    mockMvc.perform(get("/api/addresses")
        .param("memberId", "1001"))
      .andExpect(status().isOk());
  }
}