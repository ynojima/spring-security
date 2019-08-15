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

package org.springframework.security.webauthn.config.configurers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.webauthn.WebAuthn4JWebAuthnAuthenticationManager;
import org.springframework.security.webauthn.WebAuthnAuthenticationManager;
import org.springframework.security.webauthn.WebAuthnRegistrationRequestValidator;
import org.springframework.security.webauthn.challenge.HttpSessionWebAuthnChallengeRepository;
import org.springframework.security.webauthn.challenge.WebAuthnChallengeRepository;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProvider;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProviderImpl;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;

/**
 * Internal utility for WebAuthn Configurers
 */
public class WebAuthnConfigurerUtil {

	private WebAuthnConfigurerUtil() {
	}

	static <H extends HttpSecurityBuilder<H>> WebAuthnChallengeRepository getOrCreateChallengeRepository(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnChallengeRepository webAuthnChallengeRepository;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnChallengeRepository.class);
		if (beanNames.length == 0) {
			webAuthnChallengeRepository = new HttpSessionWebAuthnChallengeRepository();
		} else {
			webAuthnChallengeRepository = applicationContext.getBean(WebAuthnChallengeRepository.class);
		}
		return webAuthnChallengeRepository;
	}

	public static <H extends HttpSecurityBuilder<H>> JsonConverter getOrCreateJsonConverter(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		JsonConverter jsonConverter;
		String[] beanNames = applicationContext.getBeanNamesForType(JsonConverter.class);
		if (beanNames.length == 0) {
			ObjectMapper jsonMapper = new ObjectMapper();
			ObjectMapper cborMapper = new ObjectMapper(new CBORFactory());
			jsonConverter = new JsonConverter(jsonMapper, cborMapper);
		} else {
			jsonConverter = applicationContext.getBean(JsonConverter.class);
		}
		return jsonConverter;
	}

	public static <H extends HttpSecurityBuilder<H>> WebAuthnServerPropertyProvider getOrCreateServerPropertyProvider(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnServerPropertyProvider webAuthnServerPropertyProvider;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnServerPropertyProvider.class);
		if (beanNames.length == 0) {
			webAuthnServerPropertyProvider = new WebAuthnServerPropertyProviderImpl(getOrCreateWebAuthnAuthenticationManager(http), getOrCreateChallengeRepository(http));
		} else {
			webAuthnServerPropertyProvider = applicationContext.getBean(WebAuthnServerPropertyProvider.class);
		}
		return webAuthnServerPropertyProvider;
	}

	public static <H extends HttpSecurityBuilder<H>> WebAuthnUserDetailsService getWebAuthnUserDetailsService(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		return applicationContext.getBean(WebAuthnUserDetailsService.class);
	}

	public static <H extends HttpSecurityBuilder<H>> WebAuthnRegistrationRequestValidator getOrCreateWebAuthnRegistrationRequestValidator(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnRegistrationRequestValidator.class);
		if (beanNames.length == 0) {
			webAuthnRegistrationRequestValidator = new WebAuthnRegistrationRequestValidator(getOrCreateWebAuthnAuthenticationManager(http), getOrCreateServerPropertyProvider(http));
		} else {
			webAuthnRegistrationRequestValidator = applicationContext.getBean(WebAuthnRegistrationRequestValidator.class);
		}
		return webAuthnRegistrationRequestValidator;
	}

	public static <H extends HttpSecurityBuilder<H>> WebAuthnAuthenticationManager getOrCreateWebAuthnAuthenticationManager(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnAuthenticationManager webAuthnAuthenticationManager;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnAuthenticationManager.class);
		if (beanNames.length == 0) {
			webAuthnAuthenticationManager = new WebAuthn4JWebAuthnAuthenticationManager(
					getOrCreateWebAuthnRegistrationContextValidator(http),
					getOrCreateWebAuthnAuthenticationContextValidator(http),
					getOrCreateJsonConverter(http).getCborConverter()
			);
		} else {
			webAuthnAuthenticationManager = applicationContext.getBean(WebAuthnAuthenticationManager.class);
		}
		return webAuthnAuthenticationManager;
	}

	private static <H extends HttpSecurityBuilder<H>> WebAuthnAuthenticationContextValidator getOrCreateWebAuthnAuthenticationContextValidator(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnAuthenticationContextValidator webAuthnAuthenticationContextValidator;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnAuthenticationContextValidator.class);
		if (beanNames.length == 0) {
			webAuthnAuthenticationContextValidator = new WebAuthnAuthenticationContextValidator(getOrCreateJsonConverter(http), getOrCreateJsonConverter(http).getCborConverter());
		} else {
			webAuthnAuthenticationContextValidator = applicationContext.getBean(WebAuthnAuthenticationContextValidator.class);
		}
		return webAuthnAuthenticationContextValidator;
	}

	public static <H extends HttpSecurityBuilder<H>> WebAuthnRegistrationContextValidator getOrCreateWebAuthnRegistrationContextValidator(H http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		WebAuthnRegistrationContextValidator webAuthnRegistrationContextValidator;
		String[] beanNames = applicationContext.getBeanNamesForType(WebAuthnRegistrationContextValidator.class);
		if (beanNames.length == 0) {
			webAuthnRegistrationContextValidator = WebAuthnRegistrationContextValidator.createNonStrictRegistrationContextValidator();
		} else {
			webAuthnRegistrationContextValidator = applicationContext.getBean(WebAuthnRegistrationContextValidator.class);
		}
		return webAuthnRegistrationContextValidator;
	}
}
