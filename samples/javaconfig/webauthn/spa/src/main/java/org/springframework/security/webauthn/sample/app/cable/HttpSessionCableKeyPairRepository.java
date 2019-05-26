package org.springframework.security.webauthn.sample.app.cable;

import com.webauthn4j.util.ECUtil;
import org.springframework.security.webauthn.challenge.HttpSessionChallengeRepository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;

public class HttpSessionCableKeyPairRepository implements CableKeyPairRepository {

	// ~ Static fields/initializers
	// =====================================================================================

	private static final String DEFAULT_CABLE_KEY_ATTR_NAME = HttpSessionChallengeRepository.class
			.getName().concat(".CABLE_KEY");

	//~ Instance fields
	// ================================================================================================
	private String sessionAttributeName = DEFAULT_CABLE_KEY_ATTR_NAME;


	@Override
	public void saveCableKeyPair(KeyPair keyPair, HttpServletRequest request) {
		if (keyPair == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(this.sessionAttributeName);
			}
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(this.sessionAttributeName, keyPair);
		}

	}

	@Override
	public KeyPair loadCableKeyPair(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (KeyPair) session.getAttribute(this.sessionAttributeName);
	}

	@Override
	public KeyPair generateKeyPair() {
		return ECUtil.createKeyPair();
	}

	/**
	 * Sets the {@link HttpSession} attribute name that the {@link KeyPair} is stored in
	 *
	 * @param sessionAttributeName the new attribute name to use
	 */
	public void setSessionAttributeName(String sessionAttributeName) {
		Assert.hasLength(sessionAttributeName,
				"sessionAttributename cannot be null or empty");
		this.sessionAttributeName = sessionAttributeName;
	}

}
