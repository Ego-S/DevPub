package com.devpub.application.service;

import com.devpub.application.dto.request.ChangePasswordRequest;
import com.devpub.application.dto.request.LoginRequest;
import com.devpub.application.dto.request.RegistrationBody;
import com.devpub.application.dto.request.UserEmailRequest;
import com.devpub.application.dto.response.LoginDTO;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.dto.response.UserLoginDTO;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final AuthenticationManager authenticationManager;
	private final SendEmailService sendEmailService;
	private final UploadService uploadService;
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
					   SendEmailService sendEmailService,
					   CaptchaService captchaService,
					   UploadService uploadService,
					   AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.sendEmailService = sendEmailService;
		this.captchaService = captchaService;
		this.uploadService = uploadService;
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

	public ResultDTO registration(RegistrationBody registrationBody) {
		Map<String, String> errors = new HashMap<>();

		if (userRepository.findByEmail(registrationBody.getEmail()).isPresent()) {
			errors.put("email", USER_ALREADY_EXIST_ERROR);
		}

		//TODO what kind of errors can be on user-name field?
		if (registrationBody.getName().length() < usernameMinLength) {
			errors.put("name", USERNAME_IS_INVALID_ERROR);
		}

		if (!isPasswordValid(registrationBody.getPassword())) {
			errors.put("password", PASSWORD_TO_SHORT_ERROR);
		}

		if (!isCaptchaValid(registrationBody.getCaptcha(), registrationBody.getCaptchaSecret())) {
			errors.put("captcha", CAPTCHA_ERROR);
		}

		if (errors.size() == 0) {
			com.devpub.application.model.User user = new com.devpub.application.model.User();
			user.setName(registrationBody.getName());
			user.setPassword(passwordEncoder.encode(registrationBody.getPassword()));
			user.setEmail(registrationBody.getEmail());
			user.setRegistrationTime(LocalDateTime.now());
			userRepository.save(user);
			return new ResultDTO(true, null);
		} else {
			return new ResultDTO(false, errors);
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

	public ResultDTO restore(UserEmailRequest userEmailRequest) {
		String email = userEmailRequest.getEmail();
		Optional<com.devpub.application.model.User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return new ResultDTO(false, null);
		}

		com.devpub.application.model.User user = optionalUser.get();
		String hash = passwordEncoder
				.encode(Long.toString(System.currentTimeMillis()))
				.replaceAll("\\W", "")
				.toLowerCase();
		user.setCode(hash);
		userRepository.save(user);

		try {
			sendEmailService.sendPasswordRecoveryEmail(email, user.getName(), hash);
			return new ResultDTO(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultDTO(false, null);
		}
	}

	public ResultDTO changePassword(ChangePasswordRequest request) {
		Map<String, String> errors = new HashMap<>();
		com.devpub.application.model.User user = userRepository.findByCode(request.getCode()).orElse(null);

		//check the code
		if (user == null) {
			errors.put("code", "Ссылка для восстановления пароля устарела.\n <a href=\"/auth/restore\">Запросить " +
					"ссылку снова</a>");
		}

		//check the password
		if (!isPasswordValid(request.getPassword())) {
			errors.put("password", PASSWORD_TO_SHORT_ERROR);
		}

		//check the captcha
		if (!isCaptchaValid(request.getCaptcha(), request.getCaptchaSecret())) {
			errors.put("captcha", CAPTCHA_ERROR);
		}

		if (errors.isEmpty()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			userRepository.save(user);
			return new ResultDTO(true, null);
		} else {
			return new ResultDTO(false, errors);
		}
	}


	public ResultDTO profileRedaction(
			MultipartFile photo,
			String name,
			String email,
			String password,
			Boolean removePhoto,
			Principal principal
	) {
		Map<String, String> errors = new HashMap<>();
		com.devpub.application.model.User user = getUser(principal);

		//change name
		user.setName(name);

		//change email if it differs from authorized users email
		if (!user.getEmail().toLowerCase().equals(email.toLowerCase())) {
			if (userRepository.findByEmail(email).isPresent()) {
				errors.put("email", USER_ALREADY_EXIST_ERROR);
			} else {
				user.setEmail(email);
			}
		}

		//change password if we have to
		if (password != null) {
			if (isPasswordValid(password)) {
				user.setPassword(passwordEncoder.encode(password));
			} else {
				errors.put("password", PASSWORD_TO_SHORT_ERROR);
			}
		}

		//save photo
		if (photo != null) {
			Map<String, String> photoErrors = uploadService.checkErrors(photo);
			if (!photoErrors.isEmpty()) {
				errors.putAll(photoErrors);
			} else {
				//delete old file
				uploadService.deleteFile(user.getPhotoPath());
				//save new file
				user.setPhotoPath(uploadService.saveAvatar(photo));
			}
		}

		//remove photo
		if (removePhoto) {
			uploadService.deleteFile(user.getPhotoPath());
			user.setPhotoPath("");
		}

		//build method response
		if (errors.isEmpty()) {
			userRepository.save(user);
			return new ResultDTO(true, null);
		} else {
			return new ResultDTO(false, errors);
		}
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

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordMinLength;
	}

	private boolean isCaptchaValid(String captcha, String captchaSecret) {
		return captchaService.findCaptchaByCodeAndSecretCode(captcha, captchaSecret).isPresent();
	}

}
