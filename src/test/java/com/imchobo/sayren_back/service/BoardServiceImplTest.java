package com.imchobo.sayren_back.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@ActiveProfiles("test")
@SpringBootTest
@Log4j2
public class BoardServiceImplTest {
//  @Autowired
//  private BoardService boardService;

//  @Test
//  public void testRegister() {
//    BoardRequestDTO dto = BoardRequestDTO.builder()
//            .categoryId(1L)  // 실제 DB에 있는 카테고리 ID로 세팅
//            .productId(null) // 상품 연결 필요 없으면 null
//            .title("테스트 제목")
//            .content("테스트 내용")
//            .isSecret(false)
//            .build();
//
//    Long boardId = boardService.register(dto);
//
//    BoardResponseDTO result = boardService.read(boardId);
//    log.info("등록된 게시글: {}", result);
//    log.info("제목: {}", result.getTitle());
//    log.info("내용: {}", result.getContent());
//
//
//  }
}
