package com.devpub.application.service;

import com.devpub.application.dto.response.LoginDTO;
import com.devpub.application.dto.request.LoginRequest;
import com.devpub.application.dto.response.LogoutResponse;
import com.devpub.application.dto.response.UserLoginDTO;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

	public LogoutResponse logout(){
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Set<GrantedAuthority> grantedAuthority = new HashSet<>();
		grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		securityContext.setAuthentication(
				new AnonymousAuthenticationToken(
						String.valueOf(System.currentTimeMillis()),
						new User("anonymous", "anonymous", grantedAuthority),
						grantedAuthority
				)
		);

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
