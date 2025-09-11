package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.MemberLoginDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MemberMapper {


  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "READY")
  @Mapping(target = "emailVerified", constant = "false")
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "tel", ignore = true)
  Member toEntity(MemberSignupDTO memberSignupDTO);

  @Mapping(source = "name", target = "realName")
  @Mapping(target = "attributes", ignore = true)
  MemberAuthDTO toAuthDTO(Member entity);
}
