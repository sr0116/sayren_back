package com.imchobo.sayren_back.domain.term.repository;

import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TermRepositoryTest {
  @Autowired
  TermRepository termRepository;

  @Test
  @Transactional
  void serviceTerm(){
    Term term = Term.builder()
      .type(TermType.SERVICE)
      .title("서비스 이용약관")
      .content("<p className=\"font-bold\">세이렌 서비스 이용약관</p><p>세이렌 주식회사(이하 “회사”)가 제공하는 가전 렌탈 서비스(이하 “서비스”)를 이용해 주셔서 감사합니다. 본 약관은 회사와 이용자 간의 권리·의무 및 서비스 이용 조건을 규정합니다.</p><br/><p className=\"font-semibold\">제1조 (회원가입 및 계정)</p><p>이용자는 본 약관에 동의 후 회원가입을 신청할 수 있으며, 회사는 이메일 또는 휴대폰 인증을 요구할 수 있습니다. 회사는 원칙적으로 신청을 승낙하되, 아래 사유가 있는 경우 승낙을 거부하거나 사후에 이용계약을 해지할 수 있습니다.</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>타인의 명의를 사용하거나 허위 정보를 기재한 경우</li><li>만 14세 미만 아동이 법정대리인 동의 없이 신청한 경우</li><li>기타 회사 정책·관련 법령을 위반한 경우</li></ul><br/><p className=\"font-semibold\">제2조 (렌탈 신청 및 이용)</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>이용자는 회사가 제공하는 절차에 따라 원하는 가전제품을 선택하고, 렌탈 신청을 할 수 있습니다.</li><li>회사는 심사(신용정보 조회, 본인 확인 등)를 거쳐 승낙 여부를 결정하며, 승낙 시 렌탈 계약이 체결됩니다.</li><li>렌탈 제품의 소유권은 회사에 있으며, 이용자는 계약 기간 동안 사용권만 가집니다.</li><li>이용자는 정상적인 사용 범위를 벗어나 제품을 임의로 개조, 양도, 전대할 수 없습니다.</li></ul><br/><p className=\"font-semibold\">제3조 (요금 및 결제)</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>렌탈 요금은 계약 시점에 명시된 금액과 납부 방식(월 단위 자동결제 등)에 따릅니다.</li><li>이용자는 지정된 결제수단(신용카드, 계좌이체 등)을 통해 정해진 기일에 요금을 납부하여야 합니다.</li><li>결제일에 미납이 발생할 경우, 회사는 납부 요청을 할 수 있으며 일정 기간 이상 미납 시 서비스 이용을 제한할 수 있습니다.</li></ul><br/><p className=\"font-semibold\">제4조 (해지 및 연체)</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>이용자는 계약 기간 내 해지를 원할 경우, 회사가 정한 절차에 따라 해지 신청을 할 수 있습니다.</li><li>중도 해지 시, 약정 위약금 또는 잔여 요금이 발생할 수 있으며, 구체적인 금액은 계약서에 따릅니다.</li><li>이용자가 정해진 기일 내 요금을 연속 2회 이상 납부하지 않는 경우, 회사는 계약을 해지하고 제품 회수 조치를 취할 수 있습니다.</li><li>계약 해지 시, 이용자는 제품을 원상태로 반환하여야 하며, 파손·분실 시 별도의 비용이 청구될 수 있습니다.</li></ul><br/><p className=\"font-semibold\">제5조 (회사와 이용자의 권리·의무)</p><p className=\"font-medium\">회사의 의무</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>안정적이고 지속적인 서비스 제공을 위해 노력합니다.</li><li>제품 설치, 유지보수, A/S를 계약에 따라 제공합니다.</li><li>이용자의 개인정보를 보호하고 관련 법령을 준수합니다.</li></ul><p className=\"font-medium\">이용자의 의무</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>렌탈 제품을 정상적인 사용 범위에서 사용해야 합니다.</li><li>요금을 성실히 납부하고, 회사 정책 및 관련 법령을 준수해야 합니다.</li><li>제품의 파손, 분실, 도난 발생 시 지체 없이 회사에 통지해야 합니다.</li></ul><br/><p className=\"font-semibold\">제6조 (기타)</p><ul className=\"list-disc list-inside ml-2 text-gray-400\"><li>본 약관에 명시되지 않은 사항은 관련 법령 및 일반 상관습에 따릅니다.</li><li>본 약관에 따른 분쟁은 대한민국 법을 준거법으로 하며, 관할 법원은 민사소송법상의 관할 법원으로 합니다.</li><li>본 약관은 2025년 9월 17일부터 시행합니다.</li></ul>\n")
      .version("1.0.0")
      .required(true)
      .status(TermStatus.ACTIVE)
      .build();
    termRepository.save(term);
  }

  @Test
  @Transactional
  void privacyTerm(){
    Term term = Term.builder()
      .type(TermType.PRIVACY)
      .title("개인정보 처리방침")
      .content("<p class=\"font-bold\">세이렌(SAYREN) 개인정보처리방침</p><p>세이렌 주식회사(이하 \"회사\")는 「개인정보 보호법」 제30조에 따라 이용자의 개인정보를 보호하고, 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 다음과 같이 개인정보처리방침을 수립·공개합니다.</p><br/><p class=\"font-semibold\">1. 개인정보의 처리 목적</p><ul class=\"list-disc list-inside ml-2 text-gray-400\"><li>회원가입 및 관리: 회원 가입의사 확인, 본인 식별·인증, 계정 관리, 서비스 부정이용 방지, 고지·통지 및 민원처리</li><li>렌탈 신청 및 서비스 제공: 제품 배송, 설치, 요금 결제 및 정산, A/S 처리</li><li>마케팅 및 광고: 이벤트 및 광고성 정보 제공, 서비스 이용 통계</li></ul><br/><p class=\"font-semibold\">2. 개인정보의 처리 및 보유 기간</p><ul class=\"list-disc list-inside ml-2 text-gray-400\"><li>회원가입 및 관리: 회원 탈퇴 시까지</li><li>렌탈 신청 및 서비스 제공: 서비스 공급 완료 및 요금 결제·정산 시까지</li><li>관계 법령에 따라 일정 기간 보존할 필요가 있는 경우: 계약·결제 기록 5년, 소비자 불만 및 분쟁처리 기록 3년, 표시·광고에 관한 기록 6개월, 서비스 접속 기록 3개월</li></ul><br/><p class=\"font-semibold\">3. 처리하는 개인정보의 항목 및 수집 방법</p><p>필수항목: 이름, 이메일, 휴대전화, 주소, 결제정보 선택항목: 생년월일, 성별 수집 방법: 홈페이지, 앱, 전화, 이메일, 배송 요청 시</p><br/><p class=\"font-semibold\">4. 만 14세 미만 아동의 개인정보 처리</p><p>회사는 만 14세 미만 아동의 회원가입 시 법정대리인의 동의를 받고 최소한의 정보만 수집합니다.</p><br/><p class=\"font-semibold\">5. 개인정보의 제3자 제공</p><p>회사는 원칙적으로 이용자의 개인정보를 제3자에게 제공하지 않습니다. 다만 법령에 따라 필요한 경우에 한해 제공할 수 있습니다.</p><br/><p class=\"font-semibold\">6. 개인정보 처리 위탁</p><p>회사는 원활한 서비스 제공을 위하여 결제대행사(PG사), 배송업체 등 외부 전문업체에 개인정보 처리를 위탁할 수 있으며, 관련 법령에 따른 안전조치를 취합니다.</p><br/><p class=\"font-semibold\">7. 개인정보의 파기</p><p>개인정보 보유기간 경과, 처리 목적 달성 등 불필요해진 경우 지체 없이 파기합니다. 전자적 파일은 복구 불가능한 기술적 방법으로, 종이 문서는 분쇄 또는 소각하여 파기합니다.</p><br/><p class=\"font-semibold\">8. 정보주체의 권리·의무</p><p>이용자는 언제든지 본인의 개인정보 열람, 정정, 삭제, 처리정지를 요구할 수 있으며, 법정대리인을 통한 행사도 가능합니다.</p><br/><p class=\"font-semibold\">9. 개인정보의 안전성 확보조치</p><p>회사는 개인정보 보호를 위해 내부 관리계획 수립, 접근통제, 암호화, 보안프로그램 설치, 물리적 접근 통제 등의 조치를 취하고 있습니다.</p><br/><p class=\"font-semibold\">10. 개인정보 자동수집 장치의 설치·운영</p><p>회사는 맞춤형 서비스 제공을 위해 쿠키를 사용할 수 있으며, 이용자는 웹 브라우저 설정을 통해 쿠키 저장을 거부할 수 있습니다.</p><br/><p class=\"font-semibold\">11. 개인정보 보호책임자</p><p>개인정보 보호책임자 및 담당자를 지정하여 개인정보 관련 문의, 불만, 피해구제 요청을 처리합니다. 책임자: OOO / 연락처: OO-OOOO-OOOO / 이메일: example@sayren.com</p><br/><p class=\"font-semibold\">12. 권익침해 구제방법</p><p>개인정보 침해 관련 분쟁 해결이나 상담은 개인정보분쟁조정위원회(1833-6972), 개인정보침해신고센터(118), 대검찰청(1301), 경찰청(182) 등에 신청할 수 있습니다.</p><br/><p class=\"font-semibold\">13. 개인정보처리방침 변경</p><p>법령, 정책 변경에 따라 본 방침이 수정될 수 있으며, 변경 시 시행일자를 명시하여 공지합니다.</p>\n")
      .version("1.0.0")
      .required(true)
      .status(TermStatus.ACTIVE)
      .build();
    termRepository.save(term);
  }
}