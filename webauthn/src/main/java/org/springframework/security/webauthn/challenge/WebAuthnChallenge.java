package org.springframework.security.webauthn.challenge;

public interface WebAuthnChallenge {

	/**
	 * Gets the challenge value. Cannot be null.
	 *
	 * @return the challenge value
	 */
	byte[] getValue();
}
