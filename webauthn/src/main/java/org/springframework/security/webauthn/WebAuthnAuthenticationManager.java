package org.springframework.security.webauthn;

import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.request.WebAuthnAuthenticationRequest;
import org.springframework.security.webauthn.request.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.server.EffectiveRpIdProvider;

public interface WebAuthnAuthenticationManager extends EffectiveRpIdProvider {

	void verifyRegistrationRequest(WebAuthnRegistrationRequest webAuthnRegistrationRequest);
	void verifyAuthenticationRequest(WebAuthnAuthenticationRequest authenticationContext, WebAuthnAuthenticator authenticator);

}
