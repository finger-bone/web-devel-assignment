package com.webdev.backend.filter;

import com.auth0.jwt.interfaces.Claim;
import com.webdev.backend.util.JwtUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "JwtFilter", urlPatterns = "/secure/*")
public class JwtFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		response.setCharacterEncoding("UTF-8");
		// 获取 header里的token
		// final String token = request.getHeader("authorization");

		// 如果有bear 头，去掉bear
		String token = request.getHeader("authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			chain.doFilter(request, response);
		}
		// Except OPTIONS, other request should be checked by JWT
		else {

			if (token == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			Map<String, Claim> userData = JwtUtil.verifyToken(token);
			if (userData == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			Integer id = userData.get("id").asInt();
			String username = userData.get("username").asString();
			String password = userData.get("password").asString();
			String role = userData.get("role").asString();
			// if (!role.equals("admin")) {
			// // set to http code to unauthorized
			// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return;
			// }
			// 拦截器 拿到用户信息，放到request中
			request.setAttribute("id", id);
			request.setAttribute("username", username);
			request.setAttribute("password", password);
			request.setAttribute("role", role);
			chain.doFilter(req, res);
		}
	}

}
