package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberListResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {


  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "READY")
  @Mapping(target = "emailVerified", constant = "false")
  @Mapping(target = "tel", ignore = true)
  Member toEntity(MemberSignupDTO memberSignupDTO);

  @Mapping(source = "name", target = "realName")
  @Mapping(target = "attributes", ignore = true)
  MemberAuthDTO toAuthDTO(Member entity);

  @Mapping(source = "realName", target = "name")
  MemberLoginResponseDTO toLoginResponseDTO(MemberAuthDTO memberAuthDTO);


  FindEmailResponseDTO toFindEmailResponseDTO(Member entity);

  MemberListResponseDTO toMemberListResponseDTO(Member entity);

  MemberDetailResponseDTO toMemberDetailResponseDTO(Member entity);

}
