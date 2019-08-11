package org.springframework.security.webauthn.server;

import org.springframework.security.webauthn.challenge.WebAuthnChallenge;

import java.util.Arrays;
import java.util.Objects;

public class WebAuthnServerProperty {
	// ~ Instance fields
	// ================================================================================================

	private final WebAuthnOrigin origin;
	private final String rpId;
	private final WebAuthnChallenge challenge;
	private final byte[] tokenBindingId;

	// ~ Constructor
	// ========================================================================================================

	public WebAuthnServerProperty(WebAuthnOrigin origin, String rpId, WebAuthnChallenge challenge, byte[] tokenBindingId) {
		this.origin = origin;
		this.rpId = rpId;
		this.challenge = challenge;
		this.tokenBindingId = tokenBindingId;
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Returns the {@link WebAuthnOrigin}
	 *
	 * @return the {@link WebAuthnOrigin}
	 */
	public WebAuthnOrigin getOrigin() {
		return origin;
	}

	/**
	 * Returns the rpId
	 *
	 * @return the rpId
	 */
	public String getRpId() {
		return rpId;
	}

	/**
	 * Returns the {@link WebAuthnChallenge}
	 *
	 * @return the {@link WebAuthnChallenge}
	 */
	public WebAuthnChallenge getChallenge() {
		return challenge;
	}

	/**
	 * Returns the tokenBindingId
	 *
	 * @return the tokenBindingId
	 */
	public byte[] getTokenBindingId() {
		return tokenBindingId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WebAuthnServerProperty that = (WebAuthnServerProperty) o;
		return Objects.equals(origin, that.origin) &&
				Objects.equals(rpId, that.rpId) &&
				Objects.equals(challenge, that.challenge) &&
				Arrays.equals(tokenBindingId, that.tokenBindingId);
	}

	@Override
	public int hashCode() {

		int result = Objects.hash(origin, rpId, challenge);
		result = 31 * result + Arrays.hashCode(tokenBindingId);
		return result;
	}

}
