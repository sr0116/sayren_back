package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.common.entity.CreatedEntity;
import com.imchobo.sayren_back.en.TermStatus;
import com.imchobo.sayren_back.en.TermType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_term")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Term extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "term_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private TermType type;

  @Column(nullable = false, length = 255)
  private String title;

  @Lob // Large Object
  @Column(nullable = false)
  private String content;

  @Column(nullable = false, length = 20)
  private String version;

  // 필수 여부
  @Column(nullable = false)
  private Boolean required;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TermStatus status = TermStatus.ACTIVE;
}
