package com.imchobo.sayren_back.domain.board.dto.review;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewModifyRequestDTO extends BoardRequestDTO{
    private Long boardId; // 수정 게시글 번호
    private int rating; // 별점
    private Long productId; // 상품 번호
}
