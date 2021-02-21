package com.devpub.application.service;

import com.devpub.application.dto.LoginDTO;
import com.devpub.application.dto.LoginRequest;
import com.devpub.application.dto.LogoutResponse;
import com.devpub.application.dto.UserLoginDTO;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public UserService(UserRepository userRepository,
					   PostRepository postRepository,
					   AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.authenticationManager = authenticationManager;
	}

	public LoginDTO login(LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(
							new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			User user = (User) authentication.getPrincipal();
			return getLoginDTO(user.getUsername());
		} catch (AuthenticationException ex) {
			return new LoginDTO(false, null);
		}
	}

	public LogoutResponse logout(HttpServletRequest request, HttpServletResponse response){
		SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
		for (Cookie cookie : request.getCookies()) {
			String cookieName = cookie.getName();
			Cookie cookieToDelete = new Cookie(cookieName, null);
			cookieToDelete.setMaxAge(0);
			response.addCookie(cookieToDelete);
		}
		securityContextLogoutHandler.logout(request, response, null);
		return new LogoutResponse(true);
	}

	public LoginDTO getLoginDTO(String email) throws UsernameNotFoundException {
		try {
			com.devpub.application.model.User currentUser =
					userRepository.findByEmail(email)
							.orElseThrow(() -> new UsernameNotFoundException(email));

			UserLoginDTO userLoginDTO = mappingUserToUserLoginDTO(currentUser);

			return new LoginDTO(true, userLoginDTO);
		} catch (UsernameNotFoundException ex) {
			return new LoginDTO(false, null);
		}
	}

	private UserLoginDTO mappingUserToUserLoginDTO(com.devpub.application.model.User user) {
		return new UserLoginDTO(
				user.getId(),
				user.getName(),
				user.getPhotoPath(),
				user.getEmail(),
				user.isModerator(),
				user.isModerator() ? postRepository.countByStatus(ModerationStatus.NEW) : 0,
				user.isModerator()
		);
	}
}
