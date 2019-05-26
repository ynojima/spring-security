package org.springframework.security.webauthn.sample.app.cable;

import com.webauthn4j.data.client.challenge.Challenge;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;

public interface CableKeyPairRepository {

	/**
	 * Saves the {@link Challenge} using the {@link HttpServletRequest} and
	 * {@link HttpServletResponse}. If the {@link Challenge} is null, it is the same as
	 * deleting it.
	 *
	 * @param keyPair the {@link KeyPair} to save
	 * @param request   the {@link HttpServletRequest} to use
	 */
	void saveCableKeyPair(KeyPair keyPair, HttpServletRequest request);

	/**
	 * Loads the {@link KeyPair} associated with the {@link HttpServletRequest}
	 *
	 * @param request the {@link HttpServletRequest} to use
	 * @return the {@link KeyPair}
	 */
	KeyPair loadCableKeyPair(HttpServletRequest request);

	/**
	 * Loads or generates {@link Challenge} from the {@link HttpServletRequest}
	 *
	 * @param request the {@link HttpServletRequest} to use
	 * @return the {@link Challenge} or null if none exists
	 */
	default KeyPair loadOrGenerateCableKeyPair(HttpServletRequest request) {
		KeyPair challenge = this.loadCableKeyPair(request);
		if (challenge == null) {
			challenge = this.generateKeyPair();
			this.saveCableKeyPair(challenge, request);
		}
		return challenge;
	}

	KeyPair generateKeyPair();
}
