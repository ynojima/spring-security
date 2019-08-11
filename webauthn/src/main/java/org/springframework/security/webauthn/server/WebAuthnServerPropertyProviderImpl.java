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

package org.springframework.security.webauthn.server;

import org.springframework.security.webauthn.challenge.WebAuthnChallenge;
import org.springframework.security.webauthn.challenge.WebAuthnChallengeRepository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * {@inheritDoc}
 */
public class WebAuthnServerPropertyProviderImpl implements WebAuthnServerPropertyProvider {

	//~ Instance fields
	// ================================================================================================
	private WebAuthnChallengeRepository webAuthnChallengeRepository;
	private String rpId;

	public WebAuthnServerPropertyProviderImpl(WebAuthnChallengeRepository webAuthnChallengeRepository) {

		Assert.notNull(webAuthnChallengeRepository, "webAuthnChallengeRepository must not be null");

		this.webAuthnChallengeRepository = webAuthnChallengeRepository;
	}

	public WebAuthnServerProperty provide(HttpServletRequest request) {

		WebAuthnOrigin origin = WebAuthnOrigin.create(request);
		String effectiveRpId = getEffectiveRpId(request);
		WebAuthnChallenge challenge = webAuthnChallengeRepository.loadOrGenerateChallenge(request);

		return new WebAuthnServerProperty(origin, effectiveRpId, challenge, null); // tokenBinding is not supported by Servlet API as of 4.0
	}

	/**
	 * returns effective rpId based on request origin and configured <code>rpId</code>.
	 *
	 * @param request request
	 * @return effective rpId
	 */
	public String getEffectiveRpId(HttpServletRequest request) {
		String effectiveRpId;
		if (this.rpId != null) {
			effectiveRpId = this.rpId;
		} else {
			WebAuthnOrigin origin = WebAuthnOrigin.create(request);
			effectiveRpId = origin.getHost();
		}
		return effectiveRpId;
	}

}
