package com.imchobo.sayren_back.domain.attach.repository;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachRepository extends JpaRepository<Attach, Long> {
}
