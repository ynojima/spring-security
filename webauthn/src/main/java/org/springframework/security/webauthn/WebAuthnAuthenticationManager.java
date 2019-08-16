package org.springframework.security.webauthn;

import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.server.EffectiveRpIdProvider;

public interface WebAuthnAuthenticationManager extends EffectiveRpIdProvider {

	void verifyRegistrationData(WebAuthnRegistrationData registrationData);
	void verifyAuthenticationData(WebAuthnAuthenticationData authenticationData, WebAuthnAuthenticator authenticator);

}
