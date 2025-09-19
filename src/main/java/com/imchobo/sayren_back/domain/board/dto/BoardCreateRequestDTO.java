package com.imchobo.sayren_back.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequestDTO {
    // 카테고리 번호
    @NotNull(message = "카테고리 번호는 필수입니다.")
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
