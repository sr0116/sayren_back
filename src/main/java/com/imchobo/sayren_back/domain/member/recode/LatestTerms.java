package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.term.entity.Term;
import jakarta.validation.constraints.NotNull;

public record LatestTerms(@NotNull(message = "서비스 이용약관이 필요합니다.") Term service, @NotNull(message = "개인정보 처리방침 약관이 필요합니다.") Term privacy) {
}
