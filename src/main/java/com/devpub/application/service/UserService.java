package com.devpub.application.service;

import com.devpub.application.dto.request.RegistrationBody;
import com.devpub.application.dto.response.LoginDTO;
import com.devpub.application.dto.request.LoginRequest;
import com.devpub.application.dto.response.LogoutResponse;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.dto.response.UserLoginDTO;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final AuthenticationManager authenticationManager;
	private final CaptchaService captchaService;

	private final String USER_ALREADY_EXIST_ERROR = "User with this email already exist";
	private final String USERNAME_IS_INVALID_ERROR = "Username is too short";
	private final String PASSWORD_TO_SHORT_ERROR = "Password is too short";
	private final String CAPTCHA_ERROR = "The captcha is wrong";

	@Value("${usernameMinLength}")
	private int usernameMinLength;
	@Value("${passwordMinLength}")
	private int passwordMinLength;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository,
					   PostRepository postRepository,
					   CaptchaService captchaService,
					   AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.captchaService = captchaService;
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

	public ResponseEntity<ResultDTO> registration(RegistrationBody registrationBody) {
		Map<String, String> errors = new HashMap<>();

		if (userRepository.findByEmail(registrationBody.getEmail()).isPresent()) {
			errors.put("email", USER_ALREADY_EXIST_ERROR);
		}

		//TODO what kind of errors can be on user-name field?
		if (registrationBody.getName().length() < usernameMinLength) {
			errors.put("name", USERNAME_IS_INVALID_ERROR);
		}

		if (registrationBody.getPassword().length() < passwordMinLength) {
			errors.put("password", PASSWORD_TO_SHORT_ERROR);
		}

		if (captchaService
				.findCaptchaByCodeAndSecretCode(registrationBody.getCaptcha(), registrationBody.getCaptchaSecret())
				.isEmpty()) {
			errors.put("captcha", CAPTCHA_ERROR);
		}

		if (errors.size() == 0) {
			com.devpub.application.model.User user = new com.devpub.application.model.User();
			user.setName(registrationBody.getName());
			user.setPassword(passwordEncoder.encode(registrationBody.getPassword()));
			user.setEmail(registrationBody.getEmail());
			user.setRegistrationTime(LocalDateTime.now());
			userRepository.save(user);
			return ResponseEntity.ok(new ResultDTO(true, null));
		} else {
			return ResponseEntity.ok(new ResultDTO(false, errors));
		}
	}

	public Optional<com.devpub.application.model.User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<com.devpub.application.model.User> findById(int userId) {
		return userRepository.findById(userId);
	}

	public com.devpub.application.model.User getUser(Principal principal) {
		String email = principal.getName();
		return findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
	}

	//==========================================================================

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
