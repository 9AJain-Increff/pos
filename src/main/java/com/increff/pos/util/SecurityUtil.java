package com.increff.pos.util;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/*
https://stackoverflow.com/questions/4664893/how-to-manually-set-an-authenticated-user-in-spring-security-springmvc
*/
public class SecurityUtil {

	public static void createContext(HttpSession session) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
	}

	public static void setAuthentication(Authentication token) {
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static UserPrincipal getPrincipal() {
		Authentication token = getAuthentication();
		return token == null ? null : (UserPrincipal) getAuthentication().getPrincipal();
	}
	public static String getUserRole() {
		Authentication auth = getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "";
		}

		boolean isSupervisor = auth.getAuthorities()
				.stream()
				.anyMatch(it -> it.getAuthority().equalsIgnoreCase("supervisor"));

		return isSupervisor ? "supervisor" : "operator";
	}

}
