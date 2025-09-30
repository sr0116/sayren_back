package com.imchobo.sayren_back.domain.board.dto.review;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailsResponseDTO {
    private Long boardId;
    private String title;
    private String content;
    private boolean isSecret;

    private String categoryName;
    private int replyCount;
    private int likeCount;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private BoardAttachResponseDTO thumbnail;
    private List<BoardAttachResponseDTO> attachList;

    // 리뷰 전용
    private int rating;
    private Long productId;
    private String productName;
}
