package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.LikeRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.LikeResponseDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Like;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;

import java.util.List;

public interface LikeService {
    LikeResponseDTO clickLike(LikeRequestDTO dto);



    // 마에페이지에서 좋아요 목록 불러오기
    List<Board> getMyLikedBoards();
}
