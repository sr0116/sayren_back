package com.imchobo.sayren_back.domain.board.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResponseDTO {
    // 게시글 번호
    private Long id;
    // 제목
    private String title;

    // 작성자
    //private String memberId; // 토큰기반 인증 -> member.name 으로?

    // 카테고리 명
    private String categoryName;
    // 댓글 수
    private int replyCount;
    // 좋아요 수
    private int likeCount;
    // 작성일(등록일)(BaseEntity)
    private LocalDateTime regDate;
}
