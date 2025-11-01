package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailProviderResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailTermResponseDTO;

import java.util.List;

public record MemberInfo(MemberDetailResponseDTO memberDTO, List<MemberDetailTermResponseDTO> termList, List<MemberDetailProviderResponseDTO> providerList, boolean tfa) {
}
