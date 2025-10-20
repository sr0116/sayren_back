package com.imchobo.sayren_back.domain.subscribe.repository;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeHistoryRepository extends JpaRepository<SubscribeHistory, Long> {

  //  특정 구독의 모든 이력 조회
  List<SubscribeHistory> findBySubscribe_Id(Long subscribeId);

  //  regDate만 기준으로 조회
  Optional<SubscribeHistory> findFirstBySubscribeOrderByRegDateDesc(Subscribe subscribe);

  //  구독별 모든 이력 삭제
  @Modifying
  @Query("DELETE FROM SubscribeHistory h WHERE h.subscribe = :subscribe")
  void deleteAllBySubscribe(@Param("subscribe") Subscribe subscribe);

  @Query("""
  SELECT h
  FROM SubscribeHistory h
  WHERE h.subscribe.id = :subscribeId
  ORDER BY h.regDate DESC, h.id DESC
  LIMIT 1
""")
  Optional<SubscribeHistory> findLatestBySubscribeId(@Param("subscribeId") Long subscribeId);

  //  여러 구독  모든 이력 조회
  @Query("""
        SELECT h
        FROM SubscribeHistory h
        WHERE h.subscribe.id IN :subscribeIds
        ORDER BY h.subscribe.id DESC, h.regDate DESC
    """)
  List<SubscribeHistory> findAllBySubscribeIds(@Param("subscribeIds") List<Long> subscribeIds);

}
