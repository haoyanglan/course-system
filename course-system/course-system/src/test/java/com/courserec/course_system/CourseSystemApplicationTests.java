package com.courserec.course_system;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.config.AuthInterceptor;
import com.courserec.coursesystem.utils.JwtUtils;
import com.courserec.coursesystem.utils.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class CourseSystemApplicationTests {
	@Test
	void passwordsAreHashedAndOldPasswordsCanBeMigrated() {
		String hash = PasswordUtils.hash("secure-password");
		assertTrue(PasswordUtils.isHashed(hash));
		assertTrue(PasswordUtils.matches("secure-password", hash));
		assertFalse(PasswordUtils.matches("wrong", hash));
		assertTrue(PasswordUtils.matches("old-password", "old-password"));
	}

	@Test
	void tokenMustHaveValidSignature() {
		String token = JwtUtils.generateToken("student01", "STUDENT");
		assertEquals("student01", JwtUtils.verifyToken(token).getClaim("username").asString());
		assertThrows(JWTVerificationException.class, () -> JwtUtils.verifyToken(token + "broken"));
	}

	@Test
	void userPasswordIsNeverSerialized() throws Exception {
		SysUser user = new SysUser();
		user.setUsername("student01");
		user.setPassword("secret");
		String json = new ObjectMapper().writeValueAsString(user);
		assertFalse(json.contains("password"));
		assertTrue(json.contains("student01"));
	}

	@Test
	void protectedRoutesRequireTheRightRole() throws Exception {
		AuthInterceptor interceptor = new AuthInterceptor();
		MockHttpServletRequest anonymous = new MockHttpServletRequest("GET", "/course/list");
		MockHttpServletResponse response = new MockHttpServletResponse();
		assertFalse(interceptor.preHandle(anonymous, response, new Object()));
		assertEquals(401, response.getStatus());

		MockHttpServletRequest studentAdmin = new MockHttpServletRequest("GET", "/admin/listUsers");
		studentAdmin.addHeader("Authorization", JwtUtils.generateToken("student01", "STUDENT"));
		response = new MockHttpServletResponse();
		assertFalse(interceptor.preHandle(studentAdmin, response, new Object()));
		assertEquals(403, response.getStatus());

		MockHttpServletRequest studentCourses = new MockHttpServletRequest("GET", "/course/list");
		studentCourses.addHeader("Authorization", JwtUtils.generateToken("student01", "STUDENT"));
		response = new MockHttpServletResponse();
		assertTrue(interceptor.preHandle(studentCourses, response, new Object()));
		assertEquals("student01", studentCourses.getAttribute("authUsername"));
	}
}
