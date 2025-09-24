package com.imchobo.sayren_back.domain.board.dto.review;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailsResponseDTO extends BoardResponseDTO {
    private int rating;  // 별점
    private Long productId; // 상품 번호
    private String productName; // 상품명
}
