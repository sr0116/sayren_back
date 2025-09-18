package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;
import org.springframework.scheduling.support.SimpleTriggerContext;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequestDTO {
    // 카테고리 번호
    private Long categoryId;
    // 상품 번호
    private Long productId;
    // 게시글 제목
    private String title;
    // 게시글 본문
    private String content;
    // 비밀글 여부
    private boolean isSecret;
}
