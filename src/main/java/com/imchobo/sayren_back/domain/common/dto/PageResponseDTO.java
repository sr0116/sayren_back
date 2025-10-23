package com.imchobo.sayren_back.domain.common.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@ToString
public class PageResponseDTO<DTO, Entity> {

  private final List<DTO> list;
  private final int page;            // 현재 페이지 번호
  private final int size;            // 페이지 크기
  private final int totalPages;      // 전체 페이지 수
  private final long totalElements;  // 전체 데이터 수
  private final boolean hasPrev;     // 이전 페이지 여부
  private final boolean hasNext;     // 다음 페이지 여부

  public PageResponseDTO(Page<Entity> page, Function<Entity, DTO> mapper) {
    this.list = page.stream().map(mapper).toList();
    this.page = page.getNumber() + 1;
    this.size = page.getSize();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.hasPrev = page.hasPrevious();
    this.hasNext = page.hasNext();
  }

  public static <DTO, Entity> PageResponseDTO<DTO, Entity> of(
          Page<Entity> page,
          Function<Entity, DTO> mapper
  ) {
    return new PageResponseDTO<>(page, mapper);
  }
}