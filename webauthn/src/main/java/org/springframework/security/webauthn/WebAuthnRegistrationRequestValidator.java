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

package org.springframework.security.webauthn;

import com.webauthn4j.data.WebAuthnRegistrationContext;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.util.exception.WebAuthnException;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProvider;
import org.springframework.security.webauthn.util.ExceptionUtil;
import org.springframework.security.webauthn.util.WebAuthn4JUtil;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * A validator for WebAuthn registration request
 *
 * @author Yoshikazu Nojima
 */
public class WebAuthnRegistrationRequestValidator {

	// ~ Instance fields
	// ================================================================================================
	private WebAuthnRegistrationContextValidator registrationContextValidator;
	private WebAuthnServerPropertyProvider webAuthnServerPropertyProvider;

	private List<String> expectedRegistrationExtensionIds;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * Constructor
	 *
	 * @param registrationContextValidator validator for {@link WebAuthnRegistrationContext}
	 * @param webAuthnServerPropertyProvider       provider for {@link ServerProperty}
	 */
	public WebAuthnRegistrationRequestValidator(WebAuthnRegistrationContextValidator registrationContextValidator, WebAuthnServerPropertyProvider webAuthnServerPropertyProvider) {

		Assert.notNull(registrationContextValidator, "registrationContextValidator must not be null");
		Assert.notNull(webAuthnServerPropertyProvider, "webAuthnServerPropertyProvider must not be null");

		this.registrationContextValidator = registrationContextValidator;
		this.webAuthnServerPropertyProvider = webAuthnServerPropertyProvider;
	}

	// ~ Methods
	// ========================================================================================================

	public WebAuthnRegistrationRequestValidationResponse validate(
			HttpServletRequest httpServletRequest,
			String clientDataBase64url,
			String attestationObjectBase64url,
			Set<String> transports,
			String clientExtensionsJSON
	) {
		Assert.notNull(httpServletRequest, "httpServletRequest must not be null");
		Assert.hasText(clientDataBase64url, "clientDataBase64url must have text");
		Assert.hasText(attestationObjectBase64url, "attestationObjectBase64url must have text");
		if (transports != null) {
			transports.forEach(transport -> Assert.hasText(transport, "each transport must have text"));
		}

		WebAuthnRegistrationContext registrationContext =
				createRegistrationContext(httpServletRequest, clientDataBase64url, attestationObjectBase64url, transports, clientExtensionsJSON);

		try {
			WebAuthnRegistrationContextValidationResponse response = registrationContextValidator.validate(registrationContext);
			return new WebAuthnRegistrationRequestValidationResponse(
					response.getCollectedClientData(),
					response.getAttestationObject(),
					response.getRegistrationExtensionsClientOutputs());
		} catch (WebAuthnException e) {
			throw ExceptionUtil.wrapWithAuthenticationException(e);
		}
	}

	WebAuthnRegistrationContext createRegistrationContext(
			HttpServletRequest request,
			String clientDataBase64,
			String attestationObjectBase64,
			Set<String> transports,
			String clientExtensionsJSON) {

		byte[] clientDataBytes = Base64UrlUtil.decode(clientDataBase64);
		byte[] attestationObjectBytes = Base64UrlUtil.decode(attestationObjectBase64);
		WebAuthnServerProperty webAuthnServerProperty = webAuthnServerPropertyProvider.provide(request);
		ServerProperty serverProperty = WebAuthn4JUtil.convertToServerProperty(webAuthnServerProperty);

		return new WebAuthnRegistrationContext(
				clientDataBytes,
				attestationObjectBytes,
				transports,
				clientExtensionsJSON,
				serverProperty,
				false,
				false,
				expectedRegistrationExtensionIds);
	}

	public List<String> getExpectedRegistrationExtensionIds() {
		return expectedRegistrationExtensionIds;
	}

	public void setExpectedRegistrationExtensionIds(List<String> expectedRegistrationExtensionIds) {
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}
}
