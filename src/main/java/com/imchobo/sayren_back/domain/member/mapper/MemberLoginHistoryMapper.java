package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailLoginHistoryDTO;
import com.imchobo.sayren_back.domain.member.entity.MemberLoginHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberLoginHistoryMapper {
  
  MemberDetailLoginHistoryDTO toMemberDetailLoginHistoryDTO(MemberLoginHistory entity);
}
