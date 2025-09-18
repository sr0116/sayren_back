package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardModifyRequestDTO {
    // 수정 게시글 번호
    private Long id;
    // 수정 제목
    private String title;
    // 수정 본문
    private String content;
    // 수정 비밀글 여부
    private boolean isSecret;
}
