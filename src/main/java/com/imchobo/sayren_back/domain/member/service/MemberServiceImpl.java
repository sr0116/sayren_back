package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.annotation.ActiveMemberOnly;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.common.exception.RedisKeyNotFoundException;
import com.imchobo.sayren_back.domain.common.exception.SayrenException;
import com.imchobo.sayren_back.domain.common.service.MailService;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.common.util.SolapiUtil;
import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.dto.admin.*;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.*;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.mapper.MemberProviderMapper;
import com.imchobo.sayren_back.domain.member.mapper.MemberTermMapper;
import com.imchobo.sayren_back.domain.member.recode.MemberDetail;
import com.imchobo.sayren_back.domain.member.recode.MemberInfo;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final MemberProviderRepository memberProviderRepository;
  private final RedisUtil redisUtil;
  private final MailService mailService;
  private final SolapiUtil solapiUtil;
  private final MemberTermService memberTermService;
  private final MemberTokenService memberTokenService;
  private final MemberProviderService memberProviderService;
  private final DeletedMemberService deletedMemberService;
  private final Member2FAService member2FAService;
  private final MemberTermMapper memberTermMapper;
  private final MemberProviderMapper memberProviderMapper;


  @Override
  @Transactional
  public void register(MemberSignupDTO memberSignupDTO) {
    Member entity = memberMapper.toEntity(memberSignupDTO);
    if (memberRepository.existsByEmail(entity.getEmail())) {
      throw new EmailAlreadyExistsException();
    }

    if (memberProviderRepository.existsByEmail(entity.getEmail())) {
      throw new SocialEmailAlreadyLinkedException();
    }


    entity.setPassword(passwordEncoder.encode(entity.getPassword()));
    entity.setStatus(MemberStatus.READY);
    entity.setEmailVerified(true);
    redisUtil.deleteEmailToken(memberSignupDTO.getToken());

    Member member = memberRepository.save(entity);

    memberTermService.saveTerm(member);
  }

  @Override
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
  }


  // 이메일 인증 체크하기
  @Transactional
  @Override
  public void emailVerify(String token) {
    String email = redisUtil.getEmailByToken(token);
    log.info(email);
    log.info(token);

    if (email == null) {
      return;
    }

    Member member = findByEmail(email);
    member.setEmailVerified(true);
    redisUtil.deleteEmailToken(token);
  }

  @Override
  public Member findById(Long id) {
    return memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public void sendTel(String newTel) {
    solapiUtil.sendSms(newTel);
  }



  @Override
  public void telVerify(MemberTelDTO memberTelDTO) {
    String saveTel = redisUtil.getPhoneAuth(memberTelDTO.getPhoneAuthCode());
    if (saveTel == null || saveTel.isBlank() || !saveTel.equals(memberTelDTO.getTel())) {
      throw new TelNotMatchException();
    }
  }

  // 유저용
  @Override
  @Transactional
  public void modifyTel(MemberTelDTO memberTelDTO) {
    telVerify(memberTelDTO);
    modifyTel(memberTelDTO.getTel(), SecurityUtil.getMemberAuthDTO().getId());
  }

  // 어드민용
  @Override
  @Transactional
  public void modifyTel(AdminChangeTelDTO adminChangeTelDTO) {
    modifyTel(adminChangeTelDTO.getTel(), adminChangeTelDTO.getMemberId());
  }

  // 휴대폰 변경 구현체
  @Transactional
  public void modifyTel(String tel, Long memberId){
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    member.setTel(tel);
    member.setStatus(MemberStatus.ACTIVE);
  }



  @Override
  public FindEmailResponseDTO findEmail(MemberTelDTO memberTelDTO) {
    telVerify(memberTelDTO);
    Member member = memberRepository.findByTel(memberTelDTO.getTel()).orElse(null);
    return memberMapper.toFindEmailResponseDTO(member);
  }

  @Override
  @ActiveMemberOnly
  public Map<?, ?> getTel() {
    String tel = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new).getTel();
    return Map.of("telinfo", tel);
  }


  @Override
  public void findPassword(FindPasswordRequestDTO findPasswordRequestDTO) {
    Member member = memberRepository.findByEmail(findPasswordRequestDTO.getEmail()).orElseThrow(EmailNotFoundException::new);
    mailService.passwordResetEmail(findPasswordRequestDTO.getEmail(), member.getId());
  }

  @Override
  @Transactional
  public void changePassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
    Long memberId;
    if(SecurityUtil.isUser()){
      memberId = SecurityUtil.getMemberAuthDTO().getId();
    }
    else {
      memberId = redisUtil.getResetPassword(resetPasswordRequestDTO.getToken());
    }
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    if(passwordEncoder.matches(resetPasswordRequestDTO.getNewPassword(), member.getPassword())) {
      throw new PasswordAlreadyUseException();
    }
    member.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
    if(resetPasswordRequestDTO.getToken() != null) {
      redisUtil.deleteResetPassword(resetPasswordRequestDTO.getToken());
    }
  }


  @Override
  public void checkMail(EmailVerifyRequestDTO emailVerifyRequestDTO) {
    Member member = memberRepository.findByEmail(emailVerifyRequestDTO.getEmail()).orElse(null);
    if(member != null) {
      throw new EmailAlreadyExistsException();
    }
    mailService.emailVerification(emailVerifyRequestDTO);
  }


  @Override
  public String signupNext(String token) {
    String email = redisUtil.getEmailByToken(token);
    if(email == null) {
      throw new RedisKeyNotFoundException();
    }
    return email;
  }

  // 유저가 변경
  @Override
  @Transactional
  public MemberLoginResponseDTO changeName(ChangeNameDTO changeNameDTO) {
    return changeName(changeNameDTO.getName(), SecurityUtil.getMemberAuthDTO().getId());
  }

  // 어드민이 변경
  @Override
  @Transactional
  public void changeName(AdminChangeNameDTO adminChangeNameDTO) {
    changeName(adminChangeNameDTO.getName(), adminChangeNameDTO.getMemberId());
  }

  // changeName 구현
  @Transactional
  public MemberLoginResponseDTO changeName(String name, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);

    if(member.getName().equals(name)) {
      throw new RuntimeException();
    }
    member.setName(name);
    return memberMapper.toLoginResponseDTO(memberMapper.toAuthDTO(member));
  }


  @Override
  public void passwordCheck(PasswordCheckDTO passwordCheckDTO) {
    Member member = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
    if (!passwordEncoder.matches(passwordCheckDTO.getPassword(), member.getPassword())) {
      throw new InvalidPasswordException();
    }
  }


  // 유저용
  @Override
  @Transactional
  public void deleteMember() {
    deletedMember(SecurityUtil.getMemberAuthDTO().getId());
  }


  // 구현
  @Transactional
  public void deletedMember(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    memberTokenService.deleteMemberToken(memberId);
    memberProviderService.deleteMemberProvider(memberId);
    member2FAService.delete(memberId);
    deletedMemberService.deleteMember(member);

    member.setEmail("deleted_" + member.getId());
    member.setName("탈퇴회원");
    member.setPassword(null);
    member.setTel(null);
    member.setStatus(MemberStatus.DELETED);
  }


  @Override
  public boolean hasPassword() {
    Member member = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
    return member.getPassword() != null;
  }


  // 권한 변경
  @Override
  @Transactional
  public void changeRole(AdminSelectMemberIdDTO adminSelectMemberIdDTO) {
    Member member = findById(adminSelectMemberIdDTO.getMemberId());
    if(member.getRoles().contains(Role.ADMIN)) {
      member.getRoles().remove(Role.ADMIN);
    }
    else {
      member.getRoles().add(Role.ADMIN);
    }
  }

  // 어드민 페이지 멤버 리스트 가져오기
  @Override
  public PageResponseDTO<MemberListResponseDTO, Member> getMemberList(PageRequestDTO pageRequestDTO) {
    Page<Member> result = memberRepository.findAllByStatusNot(MemberStatus.DELETED, pageRequestDTO.getPageable());
    return PageResponseDTO.of(result, memberMapper::toMemberListResponseDTO);
  }

  @Override
  public PageResponseDTO<MemberListResponseDTO, Member> getDeleteMemberList(PageRequestDTO pageRequestDTO) {
    Page<Member> result = memberRepository.findAllByStatus(MemberStatus.DELETED, pageRequestDTO.getPageable());
    return PageResponseDTO.of(result, memberMapper::toMemberListResponseDTO);
  }

  // 어드민 페이지 멤버 상세 가져오기
  @Override
  @Transactional
  public MemberInfo getMemberInfo(Long memberId) {
    List<MemberDetail> memberDetails = memberRepository.findMemberDetail(memberId).orElse(null);
    if(memberDetails == null || memberDetails.isEmpty()){
      throw new SayrenException("USER_NOT_FOUND", "해당 유저가 없습니다.");
    }

    MemberDetailResponseDTO member = memberMapper.toMemberDetailResponseDTO(memberDetails.getFirst().member());


    Set<MemberDetailTermResponseDTO> terms = new HashSet<>();
    Set<MemberDetailProviderResponseDTO> providers = new HashSet<>();
    boolean tfa = false;

    for  (MemberDetail row : memberDetails) {
      if(row.memberTerm() != null) {
        terms.add(memberTermMapper.toMemberDetailResponseDTO(row.memberTerm()));
      }
      if(row.memberProvider() != null) {
        providers.add(memberProviderMapper.toMemberDetailResponseDTO(row.memberProvider()));
      }
      if(row.member2FA() != null) {
        tfa = true;
      }
    }

    return new MemberInfo(member, terms.stream().toList(), providers.stream().toList(), tfa);
  }

  // 어드민이 회원 상태 변경(탈퇴 처리포함)
  @Override
  @Transactional
  public void changeStatus(AdminChangeMemberStatusDTO adminChangeMemberStatusDTO) {
    Member member = memberRepository.findById(adminChangeMemberStatusDTO.getMemberId()).orElseThrow(IllegalArgumentException::new);
    if(MemberStatus.READY.equals(adminChangeMemberStatusDTO.getStatus())) {
      member.setStatus(adminChangeMemberStatusDTO.getStatus());
      member.setTel(null);
    } else if(MemberStatus.DELETED.equals(adminChangeMemberStatusDTO.getStatus())) {
      deletedMember(adminChangeMemberStatusDTO.getMemberId());
    } else {
      member.setStatus(adminChangeMemberStatusDTO.getStatus());
    }
  }
}
