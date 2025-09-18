package com.imchobo.sayren_back.domain.board.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailsResponseDTO {
    // 게시글 번호
    private Long boardId;
    // 게시글 제목
    private String title;
    // 게시글 본문
    private String content;
    // 비밀글 여부
    private boolean isSecret;

    // 작성자
    // private String memberId; // 토큰기반 인증 -> member.name 으로?

    // 카테고리 명
    private String categoryName;
    // 상품명
    private String productName;

    // 첨부파일 목록
//    private List<AttachResponseDTO> attachList;  // 아직 AttachResponseDTO 생성 안함
    // 댓글 목록
//    private List<ReplyResponseDTO> replyList;

    // 댓글 수
    private int replyCount;
    // 좋아요 수
    private int likeCount;
    // 작성일(등록일)
    private LocalDateTime regDate;
    // 수정일
    private LocalDateTime modDate;
}
