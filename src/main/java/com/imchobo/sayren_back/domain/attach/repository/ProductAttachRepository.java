package com.imchobo.sayren_back.domain.attach.repository;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttachRepository extends JpaRepository<Attach, Long> {
  // 특정 상품 모든 첨부파일
  List<Attach> findByProductId(Long productId);

  // 썸네일 하나만
  Optional<Attach> findByProductIdAndIsThumbnailTrue(Long productId);

}
