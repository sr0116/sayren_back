package com.imchobo.sayren_back.domain.board.dto;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDTO {
    private Long boardId;      // 게시글 번호
    private String title;
    private String content;

    private String categoryName; // 카테고리명
    private int replyCount;      // 댓글 수
    private int likeCount;       // 좋아요 수

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private BoardAttachResponseDTO thumbnail;        // 대표 이미지
    private List<BoardAttachResponseDTO> attachList; // 첨부파일 목록
}
