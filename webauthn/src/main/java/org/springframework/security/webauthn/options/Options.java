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


import com.webauthn4j.data.*;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;
import org.springframework.security.webauthn.endpoint.Parameters;
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
public class Options implements Serializable {

	// ~ Instance fields
	// ================================================================================================

	private PublicKeyCredentialRpEntity relyingParty;
	private WebAuthnPublicKeyCredentialUserEntity user;
	private Challenge challenge;
	private List<PublicKeyCredentialParameters> pubKeyCredParams;
	private Long registrationTimeout;
	private Long authenticationTimeout;
	private List<String> credentials;
	private AuthenticatorSelectionCriteria authenticatorSelection;
	private AttestationConveyancePreference attestation;
	private AuthenticationExtensionsClientInputs registrationExtensions;
	private AuthenticationExtensionsClientInputs authenticationExtensions;
	private Parameters parameters;

	// ~ Constructors
	// ===================================================================================================


	public Options(
			PublicKeyCredentialRpEntity relyingParty,
			WebAuthnPublicKeyCredentialUserEntity user,
			Challenge challenge,
			List<PublicKeyCredentialParameters> pubKeyCredParams,
			Long registrationTimeout,
			Long authenticationTimeout,
			List<String> credentials,
			AuthenticatorSelectionCriteria authenticatorSelection,
			AttestationConveyancePreference attestation,
			AuthenticationExtensionsClientInputs registrationExtensions,
			AuthenticationExtensionsClientInputs authenticationExtensions,
			Parameters parameters) {
		this.relyingParty = relyingParty;
		this.user = user;
		this.challenge = challenge;
		this.pubKeyCredParams = pubKeyCredParams;
		this.registrationTimeout = registrationTimeout;
		this.authenticationTimeout = authenticationTimeout;
		this.credentials = credentials;
		this.authenticatorSelection = authenticatorSelection;
		this.attestation = attestation;
		this.registrationExtensions = registrationExtensions;
		this.authenticationExtensions = authenticationExtensions;
		this.parameters = parameters;
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

	public Long getAuthenticationTimeout() {
		return authenticationTimeout;
	}

	public List<String> getCredentials() {
		return credentials;
	}

	public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
		return authenticatorSelection;
	}

	public AttestationConveyancePreference getAttestation() {
		return attestation;
	}

	public AuthenticationExtensionsClientInputs getRegistrationExtensions() {
		return registrationExtensions;
	}

	public AuthenticationExtensionsClientInputs getAuthenticationExtensions() {
		return authenticationExtensions;
	}

	public Parameters getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Options options = (Options) o;
		return Objects.equals(relyingParty, options.relyingParty) &&
				Objects.equals(user, options.user) &&
				Objects.equals(challenge, options.challenge) &&
				Objects.equals(pubKeyCredParams, options.pubKeyCredParams) &&
				Objects.equals(registrationTimeout, options.registrationTimeout) &&
				Objects.equals(authenticationTimeout, options.authenticationTimeout) &&
				Objects.equals(credentials, options.credentials) &&
				Objects.equals(authenticatorSelection, options.authenticatorSelection) &&
				attestation == options.attestation &&
				Objects.equals(registrationExtensions, options.registrationExtensions) &&
				Objects.equals(authenticationExtensions, options.authenticationExtensions) &&
				Objects.equals(parameters, options.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(relyingParty, user, challenge, pubKeyCredParams, registrationTimeout, authenticationTimeout, credentials, authenticatorSelection, attestation, registrationExtensions, authenticationExtensions, parameters);
	}
}
