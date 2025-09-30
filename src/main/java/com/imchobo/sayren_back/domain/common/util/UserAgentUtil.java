package com.imchobo.sayren_back.domain.common.util;

import com.imchobo.sayren_back.domain.member.recode.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import java.net.InetAddress;

@Component
public class UserAgentUtil {
  private final Parser parser = new Parser();

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }

    try {
      if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
        ip = "127.0.0.1";
      } else {
        InetAddress inetAddress = InetAddress.getByName(ip);
        ip = inetAddress.getHostAddress();
      }
    } catch (Exception e) {
    }

    return ip;
  }

  private String getDevice(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    if (userAgent == null) return "Unknown";

    Client client = parser.parse(userAgent);

    String browser = client.userAgent.family;
    String os = client.os.family;
    String device = client.device.family;

    return String.format("%s on %s (%s)", browser, os, device);
  }

  public UserAgent getUserAgent(HttpServletRequest request) {
    String ip = getClientIp(request);
    String device = getDevice(request);
    return new UserAgent(ip, device);
  }
}
