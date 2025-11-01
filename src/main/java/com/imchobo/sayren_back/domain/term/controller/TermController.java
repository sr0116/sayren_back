package com.imchobo.sayren_back.domain.term.controller;

import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/user/term")
@RequiredArgsConstructor
public class TermController {

  private final TermService termService;

  @GetMapping("service")
  public ResponseEntity<?> getService(){
    return ResponseEntity.ok(termService.getLatestTerm(TermType.SERVICE));
  }

  @GetMapping("privacy")
  public ResponseEntity<?> getPrivacy(){
    return ResponseEntity.ok(termService.getLatestTerm(TermType.PRIVACY));
  }

}
