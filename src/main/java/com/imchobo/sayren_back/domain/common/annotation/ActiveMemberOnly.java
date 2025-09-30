package com.imchobo.sayren_back.domain.common.annotation;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize(
        "authentication.principal.status == T(com.imchobo.sayren_back.domain.member.en.MemberStatus).ACTIVE"
)
public @interface ActiveMemberOnly {
}
