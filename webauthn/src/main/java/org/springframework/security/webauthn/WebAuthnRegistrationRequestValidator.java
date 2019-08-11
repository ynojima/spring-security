package org.springframework.security.webauthn;

import org.springframework.security.webauthn.request.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProvider;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public class WebAuthnRegistrationRequestValidator {

	private WebAuthnAuthenticationManager webAuthnAuthenticationManager;
	private WebAuthnServerPropertyProvider webAuthnServerPropertyProvider;

	public WebAuthnRegistrationRequestValidator(
			WebAuthnAuthenticationManager webAuthnAuthenticationManager,
			WebAuthnServerPropertyProvider webAuthnServerPropertyProvider) {

		this.webAuthnAuthenticationManager = webAuthnAuthenticationManager;
		this.webAuthnServerPropertyProvider = webAuthnServerPropertyProvider;
	}

	public WebAuthnRegistrationRequestVerificationResponse validate(
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
		WebAuthnServerProperty webAuthnServerProperty = webAuthnServerPropertyProvider.provide(httpServletRequest);

		WebAuthnRegistrationRequest webAuthnRegistrationRequest =
				new WebAuthnRegistrationRequest(clientDataBase64url, attestationObjectBase64url, transports, clientExtensionsJSON, webAuthnServerProperty);

		return webAuthnAuthenticationManager.verifyRegistrationRequest(webAuthnRegistrationRequest);
	}

}
