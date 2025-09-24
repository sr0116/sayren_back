package com.imchobo.sayren_back.domain.board.dto.review;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDTO extends BoardRequestDTO {
    private int rating;       // 별점
    private Long productId;   // 상품 번호
}
