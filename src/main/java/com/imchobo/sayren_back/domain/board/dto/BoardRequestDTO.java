package com.imchobo.sayren_back.domain.board.dto;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachRequestDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDTO {
    // 카테고리 번호
    @NotNull(message = "카테고리 번호는 필수입니다.")
    private Long categoryId;   // 카테고리 번호
    private String title;      // 제목
    private String content;    // 본문
    private boolean isSecret;  // 비밀글 여부 (QnA 같은 데서만 사용)

    private BoardAttachRequestDTO thumbnail;       // 대표 이미지
    private List<BoardAttachRequestDTO> attachList; // 첨부파일 목록
}
