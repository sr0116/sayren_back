package com.imchobo.sayren_back.repository;

import com.imchobo.sayren_back.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagCrawlRepository extends JpaRepository<Tag, Long> {

}
