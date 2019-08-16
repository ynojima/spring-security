package org.springframework.security.webauthn;

import com.webauthn4j.util.Base64UrlUtil;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.server.WebAuthnServerPropertyProvider;
import org.springframework.util.Assert;

import java.util.List;

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

	public void validate(WebAuthnRegistrationRequest registrationRequest) {

		Assert.notNull(registrationRequest, "target must not be null");
		Assert.notNull(registrationRequest.getHttpServletRequest(),  "httpServletRequest must not be null");

		WebAuthnServerProperty webAuthnServerProperty = webAuthnServerPropertyProvider.provide(registrationRequest.getHttpServletRequest());

		WebAuthnRegistrationData webAuthnRegistrationData =	new WebAuthnRegistrationData(
				Base64UrlUtil.decode(registrationRequest.getClientDataBase64Url()),
				Base64UrlUtil.decode(registrationRequest.getAttestationObjectBase64Url()),
				registrationRequest.getTransports(),
				registrationRequest.getClientExtensionsJSON(),
				webAuthnServerProperty,
				expectedRegistrationExtensionIds);

		webAuthnAuthenticationManager.verifyRegistrationData(webAuthnRegistrationData);
	}

	public List<String> getExpectedRegistrationExtensionIds() {
		return expectedRegistrationExtensionIds;
	}

	public void setExpectedRegistrationExtensionIds(List<String> expectedRegistrationExtensionIds) {
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}

}
