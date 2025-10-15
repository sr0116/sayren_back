package com.imchobo.sayren_back.domain.board.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListResponseDTO {
    private Long boardId;
    private String title;
    private String content;
    private String productName;
    private LocalDateTime regDate;
}
