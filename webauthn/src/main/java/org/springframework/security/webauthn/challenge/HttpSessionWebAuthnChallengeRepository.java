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

package org.springframework.security.webauthn.challenge;

import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A {@link WebAuthnChallengeRepository} implementation that stores data to HTTP session
 * <p>
 * Class design is based on {@link HttpSessionCsrfTokenRepository}
 *
 * @author Yoshikazu Nojima
 */
public class HttpSessionWebAuthnChallengeRepository implements WebAuthnChallengeRepository {

	// ~ Static fields/initializers
	// =====================================================================================

	private static final String DEFAULT_CHALLENGE_ATTR_NAME = HttpSessionWebAuthnChallengeRepository.class
			.getName().concat(".CHALLENGE");

	//~ Instance fields
	// ================================================================================================
	private String sessionAttributeName = DEFAULT_CHALLENGE_ATTR_NAME;

	// ~ Methods
	// ========================================================================================================

	@Override
	public WebAuthnChallenge generateChallenge() {
		return new WebAuthnChallengeImpl();
	}

	@Override
	public void saveChallenge(WebAuthnChallenge challenge, HttpServletRequest request) {
		if (challenge == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(this.sessionAttributeName);
			}
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(this.sessionAttributeName, challenge);
		}
	}

	@Override
	public WebAuthnChallenge loadChallenge(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (WebAuthnChallenge) session.getAttribute(this.sessionAttributeName);
	}

	/**
	 * Sets the {@link HttpSession} attribute name that the {@link WebAuthnChallenge} is stored in
	 *
	 * @param sessionAttributeName the new attribute name to use
	 */
	public void setSessionAttributeName(String sessionAttributeName) {
		Assert.hasLength(sessionAttributeName,
				"sessionAttributename cannot be null or empty");
		this.sessionAttributeName = sessionAttributeName;
	}

}
