package com.imchobo.sayren_back.domain.board.dto.review;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDTO {
    private Long productId;       // 상품 번호
    private String title;         // 제목
    private String content;       // 본문
    private boolean isSecret;     // 비밀글 여부 (리뷰면 안 써도 됨)
}
