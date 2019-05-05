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

package org.springframework.security.webauthn.options;


import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;
import com.webauthn4j.util.CollectionUtil;
import org.springframework.security.webauthn.endpoint.WebAuthnPublicKeyCredentialUserEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Options for WebAuthn attestation generation
 *
 * @author Yoshikazu Nojima
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationOptions implements Serializable {

	// ~ Instance fields
	// ================================================================================================

	private PublicKeyCredentialRpEntity relyingParty;
	private WebAuthnPublicKeyCredentialUserEntity user;
	private Challenge challenge;
	private List<PublicKeyCredentialParameters> pubKeyCredParams;
	private Long registrationTimeout;
	private List<String> credentials;
	private AuthenticationExtensionsClientInputs registrationExtensions;

	// ~ Constructors
	// ===================================================================================================

	public AttestationOptions(
			PublicKeyCredentialRpEntity relyingParty,
			WebAuthnPublicKeyCredentialUserEntity user,
			Challenge challenge,
			List<PublicKeyCredentialParameters> pubKeyCredParams,
			Long registrationTimeout,
			List<String> credentials,
			AuthenticationExtensionsClientInputs registrationExtensions) {
		this.relyingParty = relyingParty;
		this.user = user;
		this.challenge = challenge;
		this.pubKeyCredParams = CollectionUtil.unmodifiableList(pubKeyCredParams);
		this.registrationTimeout = registrationTimeout;
		this.credentials = CollectionUtil.unmodifiableList(credentials);
		this.registrationExtensions = registrationExtensions;
	}

	/**
	 * Returns PublicKeyCredentialRpEntity
	 *
	 * @return PublicKeyCredentialRpEntity
	 */
	public PublicKeyCredentialRpEntity getRelyingParty() {
		return relyingParty;
	}

	/**
	 * If authenticated, returns {@link WebAuthnPublicKeyCredentialUserEntity}, which is a serialized form of {@link PublicKeyCredentialUserEntity}
	 * Otherwise returns null
	 *
	 * @return {@link WebAuthnPublicKeyCredentialUserEntity}
	 */
	public WebAuthnPublicKeyCredentialUserEntity getUser() {
		return user;
	}

	/**
	 * Returns {@link Challenge}
	 *
	 * @return {@link Challenge}
	 */
	public Challenge getChallenge() {
		return challenge;
	}

	public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
		return pubKeyCredParams;
	}

	public Long getRegistrationTimeout() {
		return registrationTimeout;
	}

	public List<String> getCredentials() {
		return credentials;
	}

	public AuthenticationExtensionsClientInputs getRegistrationExtensions() {
		return registrationExtensions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AttestationOptions that = (AttestationOptions) o;
		return Objects.equals(relyingParty, that.relyingParty) &&
				Objects.equals(user, that.user) &&
				Objects.equals(challenge, that.challenge) &&
				Objects.equals(pubKeyCredParams, that.pubKeyCredParams) &&
				Objects.equals(registrationTimeout, that.registrationTimeout) &&
				Objects.equals(credentials, that.credentials) &&
				Objects.equals(registrationExtensions, that.registrationExtensions);
	}

	@Override
	public int hashCode() {

		return Objects.hash(relyingParty, user, challenge, pubKeyCredParams, registrationTimeout, credentials, registrationExtensions);
	}
}
