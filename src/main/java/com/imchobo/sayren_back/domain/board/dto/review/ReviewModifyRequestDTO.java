package com.imchobo.sayren_back.domain.board.dto.review;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewModifyRequestDTO{
    private Long productId;
    private String title;
    private String content;
}
