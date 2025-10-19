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

@RestController
@RequestMapping("/api/user/board")
@RequiredArgsConstructor
public class UserLikeController {
    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<LikeResponseDTO> toggleLike(
            @RequestBody LikeRequestDTO dto) {

        LikeResponseDTO result = likeService.clickLike(dto);
        return ResponseEntity.ok(result);
    }

    /**
     * 좋아요(찜) 목록 조회
     */
    @GetMapping("/like/list")
    public ResponseEntity<List<Board>> getMyLikedBoards() {
        List<Board> likedBoards = likeService.getMyLikedBoards();
        return ResponseEntity.ok(likedBoards);
    }
}
