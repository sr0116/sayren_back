package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Like;
import com.imchobo.sayren_back.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
  // 좋아요 카운트
//  int countByBoardId(Long boardId);

    // 특정 회원이 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<Like> findByMemberAndBoard(Member member, Board board);

    // 좋아요 개수 (게시글 기준)
    int countByBoard(Board board);

    List<Like> findByMember(Member member);

    // 좋아요 삭제
    void deleteByMemberAndBoard(Member member, Board board);


}
