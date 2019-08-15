package org.springframework.security.webauthn;

import org.springframework.security.webauthn.request.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProvider;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public class WebAuthnRegistrationRequestValidator {

	private WebAuthnAuthenticationManager webAuthnAuthenticationManager;
	private WebAuthnServerPropertyProvider webAuthnServerPropertyProvider;

	private List<String> expectedRegistrationExtensionIds;

	public WebAuthnRegistrationRequestValidator(
			WebAuthnAuthenticationManager webAuthnAuthenticationManager,
			WebAuthnServerPropertyProvider webAuthnServerPropertyProvider) {

		this.webAuthnAuthenticationManager = webAuthnAuthenticationManager;
		this.webAuthnServerPropertyProvider = webAuthnServerPropertyProvider;
	}

	public void validate(
			HttpServletRequest httpServletRequest,
			String clientDataBase64url,
			String attestationObjectBase64url,
			Set<String> transports,
			String clientExtensionsJSON
	) {
		Assert.notNull(httpServletRequest, "httpServletRequest must not be null");
		WebAuthnServerProperty webAuthnServerProperty = webAuthnServerPropertyProvider.provide(httpServletRequest);

		WebAuthnRegistrationRequest webAuthnRegistrationRequest =
				new WebAuthnRegistrationRequest(clientDataBase64url, attestationObjectBase64url, transports, clientExtensionsJSON, webAuthnServerProperty, expectedRegistrationExtensionIds);

		webAuthnAuthenticationManager.verifyRegistrationRequest(webAuthnRegistrationRequest);
	}

	public List<String> getExpectedRegistrationExtensionIds() {
		return expectedRegistrationExtensionIds;
	}

	public void setExpectedRegistrationExtensionIds(List<String> expectedRegistrationExtensionIds) {
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}

}
