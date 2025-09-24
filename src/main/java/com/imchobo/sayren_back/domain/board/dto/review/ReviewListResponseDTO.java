package com.imchobo.sayren_back.domain.board.dto.review;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListResponseDTO {
    private Long boardId; // 게시글 번호
    private String title; // 제목
    private String productName; // 상품명
    private int rating; // 별점
    private LocalDateTime regDate; // 등록일
    private String thumbnailUrl; // 썸네일만 보여줄 경우
}
