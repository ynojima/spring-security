/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.webauthn.sample.app.web;

import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.AuthenticatorDataConverter;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.util.UUIDUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.MultiFactorAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.webauthn.WebAuthnOptionWebHelper;
import org.springframework.security.webauthn.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.WebAuthnRegistrationRequestValidator;
import org.springframework.security.webauthn.exception.DataConversionException;
import org.springframework.security.webauthn.exception.ValidationException;
import org.springframework.security.webauthn.sample.domain.entity.AuthenticatorEntity;
import org.springframework.security.webauthn.sample.domain.entity.UserEntity;
import org.springframework.security.webauthn.userdetails.InMemoryWebAuthnUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Login controller
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class WebAuthnSampleController {

	private final Log logger = LogFactory.getLog(getClass());

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String REDIRECT_SIGNUP = "redirect:/signup";

	private static final String VIEW_SIGNUP_SIGNUP = "signup/signup";
	private static final String VIEW_LOGIN_LOGIN = "login/login";
	private static final String VIEW_LOGIN_AUTHENTICATOR_LOGIN = "login/authenticator-login";

	private static final String VIEW_DASHBOARD_DASHBOARD = "dashboard/dashboard";

	@Autowired
	private InMemoryWebAuthnUserDetailsManager webAuthnUserDetailsService;

	@Autowired
	private WebAuthnRegistrationRequestValidator registrationRequestValidator;

	@Autowired
	private WebAuthnOptionWebHelper webAuthnOptionWebHelper;

	@Autowired
	private AttestationObjectConverter attestationObjectConverter;

	@Autowired
	private AuthenticatorDataConverter authenticatorDataConverter;

	@Autowired
	private AttestedCredentialDataConverter attestedCredentialDataConverter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("webAuthnChallenge", webAuthnOptionWebHelper.getChallenge(request));
		model.addAttribute("webAuthnCredentialIds", webAuthnOptionWebHelper.getCredentialIds(username));
	}

	@RequestMapping(value = "/")
	public String index(Model model) {
		return REDIRECT_SIGNUP;
	}

	@RequestMapping(value = "/dashboard")
	public String dashboard(Model model) {
		return VIEW_DASHBOARD_DASHBOARD;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String template(Model model) {
		UserCreateForm userCreateForm = new UserCreateForm();
		UUID userHandle = UUID.randomUUID();
		String userHandleStr = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(UUIDUtil.convertUUIDToBytes(userHandle));
		userCreateForm.setUserHandle(userHandleStr);
		model.addAttribute("userForm", userCreateForm);
		return VIEW_SIGNUP_SIGNUP;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String create(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute("userForm") UserCreateForm userCreateForm, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return VIEW_SIGNUP_SIGNUP;
		}

		WebAuthnRegistrationRequest webAuthnRegistrationRequest = new WebAuthnRegistrationRequest(
				request,
				userCreateForm.getAuthenticator().getClientDataJSON(),
				userCreateForm.getAuthenticator().getAttestationObject(),
				null, //TODO
				userCreateForm.getAuthenticator().getClientExtensions()
		);
		try {
			registrationRequestValidator.validate(webAuthnRegistrationRequest);
		}
		catch (ValidationException e){
			logger.debug("WebAuthn registration request validation failed.", e);
			return VIEW_SIGNUP_SIGNUP;
		}
		catch (DataConversionException e){
			logger.debug("WebAuthn registration request data conversion failed.", e);
			return VIEW_SIGNUP_SIGNUP;
		}

		UserEntity destination = new UserEntity();

		destination.setUserHandle(Base64Utils.decodeFromUrlSafeString(userCreateForm.getUserHandle()));
		destination.setUsername(userCreateForm.getUsername());
		destination.setPassword(passwordEncoder.encode(userCreateForm.getPassword()));

		List<AuthenticatorEntity> authenticators = new ArrayList<>();

		AuthenticatorEntity authenticator = new AuthenticatorEntity();
		AuthenticatorCreateForm sourceAuthenticator = userCreateForm.getAuthenticator();

		byte[] attestationObject = Base64UrlUtil.decode(sourceAuthenticator.getAttestationObject());
		byte[] authenticatorData = attestationObjectConverter.extractAuthenticatorData(attestationObject);
		byte[] attestedCredentialData = authenticatorDataConverter.extractAttestedCredentialData(authenticatorData);
		byte[] credentialId = attestedCredentialDataConverter.extractCredentialId(attestedCredentialData);
		long signCount = authenticatorDataConverter.extractSignCount(authenticatorData);

		authenticator.setUser(destination);
		authenticator.setCredentialId(credentialId);
		authenticator.setName(null); // sample application doesn't name authenticator
		authenticator.setAttestationObject(attestationObject);
		authenticator.setCounter(signCount);
		authenticator.setTransports(sourceAuthenticator.getTransports());
		authenticator.setClientExtensions(sourceAuthenticator.getClientExtensions());
		authenticators.add(authenticator);

		destination.setAuthenticators(authenticators);
		destination.setLocked(false);

		UserEntity user = destination;
		try {
			webAuthnUserDetailsService.createUser(user);
		} catch (IllegalArgumentException ex) {
			return VIEW_SIGNUP_SIGNUP;
		}

		return REDIRECT_LOGIN;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof MultiFactorAuthenticationToken) {
			return VIEW_LOGIN_AUTHENTICATOR_LOGIN;
		} else {
			return VIEW_LOGIN_LOGIN;
		}
	}

}
