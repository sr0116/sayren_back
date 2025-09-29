package com.imchobo.sayren_back.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;   // 현재 페이지 번호
    @Builder.Default
    private int size = 10;  // 한 페이지당 게시글 수

    private String type;    // 검색 조건 (t, c, w)
    private String keyword; // 검색 키워드

    public int getSkip() {
        return (page - 1) * size;
    }
}
