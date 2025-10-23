package com.imchobo.sayren_back.domain.board.controller.user;

import com.imchobo.sayren_back.domain.board.dto.LikeRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.LikeResponseDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.service.LikeService;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class UserMyPageController {
    private final LikeService likeService;

    // 내가 찜한 상품 목록 조회
    @GetMapping("/like")
    public ResponseEntity<List<Board>> getMyLikedProducts() {
        List<Board> likedBoards = likeService.getMyLikedBoards();
        return ResponseEntity.ok(likedBoards);
    }

    // 좋아요 토글 (선택적으로 유지 가능)
    @PostMapping("/like")
    public ResponseEntity<LikeResponseDTO> toggleLike(@RequestBody Map<String, Long> req) {
        Long boardId = req.get("boardId");
        LikeResponseDTO result = likeService.clickLike(
                new LikeRequestDTO(boardId, null)
        );
        return ResponseEntity.ok(result);
    }
}
