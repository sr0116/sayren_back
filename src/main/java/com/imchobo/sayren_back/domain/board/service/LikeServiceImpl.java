package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.LikeRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.LikeResponseDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Like;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.LikeRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService{
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public LikeResponseDTO clickLike(LikeRequestDTO dto) {
        if (dto.getBoardId() == null) {
            throw new IllegalArgumentException("게시글 ID가 필요합니다.");
        }

        // 로그인한 유저 가져오기
        MemberAuthDTO auth = SecurityUtil.getMemberAuthDTO();
        Member member = memberRepository.findById(auth.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 게시글 조회
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 기존 좋아요 여부 확인
        Optional<Like> existing = likeRepository.findByMemberAndBoard(member, board);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            likeRepository.save(Like.builder()
                    .member(member)
                    .board(board)
                    .build());
        }

        int likeCount = likeRepository.countByBoard(board);

        return LikeResponseDTO.builder()
                .id(board.getId())
                .likeCount(likeCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> getMyLikedBoards() {
        MemberAuthDTO auth = SecurityUtil.getMemberAuthDTO();
        Member member = memberRepository.findById(auth.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        // 내가 좋아요 누른 목록 불러오기
        List<Like> likes = likeRepository.findByMember(member);

        // Board 엔티티 목록만 추출해서 반환
        return likes.stream()
                .map(Like::getBoard)
                .toList();
    }

}
