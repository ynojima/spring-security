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

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.data.*;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.util.Base64UrlUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.webauthn.WebAuthnProcessingFilter;
import org.springframework.security.webauthn.challenge.ChallengeRepository;
import org.springframework.security.webauthn.endpoint.Parameters;
import org.springframework.security.webauthn.endpoint.WebAuthnPublicKeyCredentialUserEntity;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetails;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import org.springframework.security.webauthn.util.ServletUtil;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An {@link OptionsProvider} implementation
 *
 * @author Yoshikazu Nojima
 */
public class OptionsProviderImpl implements OptionsProvider {

	//~ Instance fields
	// ================================================================================================
	private String rpId = null;
	private String rpName = null;
	private String rpIcon = null;
	private List<PublicKeyCredentialParameters> pubKeyCredParams = new ArrayList<>();
	private AuthenticatorSelectionCriteria authenticatorSelection = new AuthenticatorSelectionCriteria(null, false, UserVerificationRequirement.PREFERRED);
	private AttestationConveyancePreference attestation = AttestationConveyancePreference.NONE;
	private Long registrationTimeout = null;
	private Long authenticationTimeout = null;
	private RegistrationExtensionsOptionProvider registrationExtensions = new RegistrationExtensionsOptionProvider();
	private AuthenticationExtensionsOptionProvider authenticationExtensions = new AuthenticationExtensionsOptionProvider();

	private String usernameParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
	private String credentialIdParameter = WebAuthnProcessingFilter.SPRING_SECURITY_FORM_CREDENTIAL_ID_KEY;
	private String clientDataJSONParameter = WebAuthnProcessingFilter.SPRING_SECURITY_FORM_CLIENT_DATA_JSON_KEY;
	private String authenticatorDataParameter = WebAuthnProcessingFilter.SPRING_SECURITY_FORM_AUTHENTICATOR_DATA_KEY;
	private String signatureParameter = WebAuthnProcessingFilter.SPRING_SECURITY_FORM_SIGNATURE_KEY;
	private String clientExtensionsJSONParameter = WebAuthnProcessingFilter.SPRING_SECURITY_FORM_CLIENT_EXTENSIONS_JSON_KEY;

	private WebAuthnUserDetailsService userDetailsService;
	private ChallengeRepository challengeRepository;

	// ~ Constructors
	// ===================================================================================================

	public OptionsProviderImpl(WebAuthnUserDetailsService userDetailsService, ChallengeRepository challengeRepository) {

		Assert.notNull(userDetailsService, "userDetailsService must not be null");
		Assert.notNull(challengeRepository, "challengeRepository must not be null");

		this.userDetailsService = userDetailsService;
		this.challengeRepository = challengeRepository;
	}


	// ~ Methods
	// ========================================================================================================

	public Options getOptions(HttpServletRequest request, String username, Challenge challenge) {

		WebAuthnPublicKeyCredentialUserEntity user;
		Collection<? extends Authenticator> authenticators;

		try {
			WebAuthnUserDetails userDetails = userDetailsService.loadUserByUsername(username);
			authenticators = userDetails.getAuthenticators();
			String userHandle = Base64UrlUtil.encodeToString(userDetails.getUserHandle());
			user = new WebAuthnPublicKeyCredentialUserEntity(userHandle, username);
		} catch (UsernameNotFoundException e) {
			authenticators = Collections.emptyList();
			user = null;
		}

		List<String> credentials = new ArrayList<>();
		for (Authenticator authenticator : authenticators) {
			String credentialId = Base64UrlUtil.encodeToString(authenticator.getAttestedCredentialData().getCredentialId());
			credentials.add(credentialId);
		}

		PublicKeyCredentialRpEntity relyingParty = new PublicKeyCredentialRpEntity(getEffectiveRpId(request), rpName, rpIcon);
		if (challenge == null) {
			challenge = challengeRepository.loadOrGenerateChallenge(request);
		} else {
			challengeRepository.saveChallenge(challenge, request);
		}
		Parameters parameters
				= new Parameters(usernameParameter, passwordParameter,
				credentialIdParameter, clientDataJSONParameter, authenticatorDataParameter, signatureParameter, clientExtensionsJSONParameter);


		return new Options(relyingParty, user, challenge, pubKeyCredParams, registrationTimeout, authenticationTimeout,
				credentials, authenticatorSelection, attestation, registrationExtensions.provide(request), authenticationExtensions.provide(request), parameters);
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
			Origin origin = ServletUtil.getOrigin(request);
			effectiveRpId = origin.getHost();
		}
		return effectiveRpId;
	}

	/**
	 * returns configured rpId
	 *
	 * @return rpId
	 */
	public String getRpId() {
		return rpId;
	}

	/**
	 * configures rpId
	 *
	 * @param rpId rpId
	 */
	public void setRpId(String rpId) {
		this.rpId = rpId;
	}

	/**
	 * returns rpName
	 *
	 * @return rpName
	 */
	public String getRpName() {
		return rpName;
	}

	/**
	 * configures rpName
	 *
	 * @param rpName rpName
	 */
	public void setRpName(String rpName) {
		Assert.hasText(rpName, "rpName parameter must not be empty or null");
		this.rpName = rpName;
	}

	/**
	 * returns rpIcon
	 *
	 * @return rpIcon
	 */
	public String getRpIcon() {
		return rpIcon;
	}

	/**
	 * configures rpIcon
	 *
	 * @param rpIcon rpIcon
	 */
	public void setRpIcon(String rpIcon) {
		Assert.hasText(rpIcon, "rpIcon parameter must not be empty or null");
		this.rpIcon = rpIcon;
	}

	/**
	 * returns {@link PublicKeyCredentialParameters} list
	 *
	 * @return {@link PublicKeyCredentialParameters} list
	 */
	public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
		return pubKeyCredParams;
	}

	/**
	 * configures pubKeyCredParams
	 *
	 * @param pubKeyCredParams {@link PublicKeyCredentialParameters} list
	 */
	public void setPubKeyCredParams(List<PublicKeyCredentialParameters> pubKeyCredParams) {
		this.pubKeyCredParams = pubKeyCredParams;
	}

	/**
	 * returns the registration timeout
	 *
	 * @return the registration timeout
	 */
	public Long getRegistrationTimeout() {
		return registrationTimeout;
	}

	/**
	 * configures the registration timeout
	 *
	 * @param registrationTimeout registration timeout
	 */
	public void setRegistrationTimeout(Long registrationTimeout) {
		Assert.isTrue(registrationTimeout >= 0, "registrationTimeout must be within unsigned long.");
		this.registrationTimeout = registrationTimeout;
	}

	/**
	 * returns the authentication timeout
	 *
	 * @return the authentication timeout
	 */
	public Long getAuthenticationTimeout() {
		return authenticationTimeout;
	}

	/**
	 * configures the authentication timeout
	 *
	 * @param authenticationTimeout authentication timeout
	 */
	public void setAuthenticationTimeout(Long authenticationTimeout) {
		Assert.isTrue(registrationTimeout >= 0, "registrationTimeout must be within unsigned long.");
		this.authenticationTimeout = authenticationTimeout;
	}

	/**
	 * returns the {@link AuthenticatorSelectionCriteria}
	 *
	 * @return the {@link AuthenticatorSelectionCriteria}
	 */
	public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
		return authenticatorSelection;
	}

	/**
	 * configures the {@link AuthenticatorSelectionCriteria}
	 *
	 * @param authenticatorSelection the {@link AuthenticatorSelectionCriteria}
	 */
	public void setAuthenticatorSelection(AuthenticatorSelectionCriteria authenticatorSelection) {
		this.authenticatorSelection = authenticatorSelection;
	}

	/**
	 * returns the {@link AttestationConveyancePreference}
	 *
	 * @return the {@link AttestationConveyancePreference}
	 */
	public AttestationConveyancePreference getAttestation() {
		return attestation;
	}

	/**
	 * configures the {@link AttestationConveyancePreference}
	 *
	 * @param attestation the {@link AttestationConveyancePreference}
	 */
	public void setAttestation(AttestationConveyancePreference attestation) {
		this.attestation = attestation;
	}

	public RegistrationExtensionsOptionProvider getRegistrationExtensions() {
		return registrationExtensions;
	}

	public AuthenticationExtensionsOptionProvider getAuthenticationExtensions() {
		return authenticationExtensions;
	}

	public String getUsernameParameter() {
		return usernameParameter;
	}

	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "usernameParameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}

	public String getPasswordParameter() {
		return passwordParameter;
	}

	public void setPasswordParameter(String passwordParameter) {
		Assert.hasText(passwordParameter, "passwordParameter must not be empty or null");
		this.passwordParameter = passwordParameter;
	}

	public String getCredentialIdParameter() {
		return credentialIdParameter;
	}

	public void setCredentialIdParameter(String credentialIdParameter) {
		Assert.hasText(credentialIdParameter, "credentialIdParameter must not be empty or null");
		this.credentialIdParameter = credentialIdParameter;
	}

	public String getClientDataJSONParameter() {
		return clientDataJSONParameter;
	}

	public void setClientDataJSONParameter(String clientDataJSONParameter) {
		Assert.hasText(clientDataJSONParameter, "clientDataJSONParameter must not be empty or null");
		this.clientDataJSONParameter = clientDataJSONParameter;
	}

	public String getAuthenticatorDataParameter() {
		return authenticatorDataParameter;
	}

	public void setAuthenticatorDataParameter(String authenticatorDataParameter) {
		Assert.hasText(authenticatorDataParameter, "authenticatorDataParameter must not be empty or null");
		this.authenticatorDataParameter = authenticatorDataParameter;
	}

	public String getSignatureParameter() {
		return signatureParameter;
	}

	public void setSignatureParameter(String signatureParameter) {
		Assert.hasText(signatureParameter, "signatureParameter must not be empty or null");
		this.signatureParameter = signatureParameter;
	}

	public String getClientExtensionsJSONParameter() {
		return clientExtensionsJSONParameter;
	}

	public void setClientExtensionsJSONParameter(String clientExtensionsJSONParameter) {
		Assert.hasText(clientExtensionsJSONParameter, "clientExtensionsJSONParameter must not be empty or null");
		this.clientExtensionsJSONParameter = clientExtensionsJSONParameter;
	}

}
