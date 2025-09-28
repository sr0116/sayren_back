package com.imchobo.sayren_back.domain.board.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponseDTO<E> {
    private List<E> list;  // 실제 데이터
    private int page;      // 현재 페이지
    private int size;      // 페이지 크기
    private int totalPage; // 전체 페이지 수

    private int start;     // 시작 페이지 번호
    private int end;       // 끝 페이지 번호
    private boolean prev;  // 이전 버튼 여부
    private boolean next;  // 다음 버튼 여부
    private List<Integer> pageList; // 페이지 번호 목록
}
