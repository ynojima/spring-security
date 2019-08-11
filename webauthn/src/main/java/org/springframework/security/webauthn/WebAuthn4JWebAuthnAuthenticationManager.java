package org.springframework.security.webauthn;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.WebAuthnRegistrationContext;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.util.exception.WebAuthnException;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.request.WebAuthnAuthenticationRequest;
import org.springframework.security.webauthn.request.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.util.ExceptionUtil;
import org.springframework.security.webauthn.util.WebAuthn4JUtil;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

public class WebAuthn4JWebAuthnAuthenticationManager implements WebAuthnAuthenticationManager {

	// ~ Instance fields
	// ================================================================================================
	private WebAuthnRegistrationContextValidator registrationContextValidator;
	private WebAuthnAuthenticationContextValidator authenticationContextValidator;

	private CborConverter cborConverter;

	private String rpId;
	private List<String> expectedRegistrationExtensionIds;

	public WebAuthn4JWebAuthnAuthenticationManager(
			WebAuthnRegistrationContextValidator registrationContextValidator,
			WebAuthnAuthenticationContextValidator authenticationContextValidator,
			CborConverter cborConverter) {
		this.registrationContextValidator = registrationContextValidator;
		this.authenticationContextValidator = authenticationContextValidator;
		this.cborConverter = cborConverter;
	}

	public WebAuthnRegistrationRequestVerificationResponse verifyRegistrationRequest(
			WebAuthnRegistrationRequest webAuthnRegistrationRequest
	) {

		if (webAuthnRegistrationRequest.getTransports() != null) {
			webAuthnRegistrationRequest.getTransports().forEach(transport -> Assert.hasText(transport, "each transport must have text"));
		}

		WebAuthnRegistrationContext registrationContext =
				createRegistrationContext(webAuthnRegistrationRequest);

		try {
			WebAuthnRegistrationContextValidationResponse response = registrationContextValidator.validate(registrationContext);
			return new WebAuthnRegistrationRequestVerificationResponse(
					response.getCollectedClientData(),
					response.getAttestationObject(),
					response.getRegistrationExtensionsClientOutputs());
		} catch (WebAuthnException e) {
			throw ExceptionUtil.wrapWithAuthenticationException(e);
		}
	}

	@Override
	public void verifyAuthenticationRequest(WebAuthnAuthenticationRequest webAuthnAuthenticationRequest, WebAuthnAuthenticator webAuthnAuthenticator) {


		ServerProperty serverProperty = WebAuthn4JUtil.convertToServerProperty(webAuthnAuthenticationRequest.getServerProperty());

		WebAuthnAuthenticationContext authenticationContext = new WebAuthnAuthenticationContext(
				webAuthnAuthenticationRequest.getCredentialId(),
				webAuthnAuthenticationRequest.getClientDataJSON(),
				webAuthnAuthenticationRequest.getAuthenticatorData(),
				webAuthnAuthenticationRequest.getSignature(),
				webAuthnAuthenticationRequest.getClientExtensionsJSON(),
				serverProperty,
				webAuthnAuthenticationRequest.isUserVerificationRequired(),
				webAuthnAuthenticationRequest.isUserPresenceRequired(),
				webAuthnAuthenticationRequest.getExpectedAuthenticationExtensionIds()
		);

		AttestedCredentialData attestedCredentialData = cborConverter.readValue(webAuthnAuthenticator.getAttestedCredentialData(), AttestedCredentialData.class);
		AttestationStatement attestationStatement = cborConverter.readValue(webAuthnAuthenticator.getAttestationStatement(), AttestationStatement.class);
		Authenticator authenticator = new AuthenticatorImpl(attestedCredentialData, attestationStatement, webAuthnAuthenticator.getCounter(), null); //TODO

		try {
			authenticationContextValidator.validate(authenticationContext, authenticator);
		} catch (WebAuthnException e) {
			throw ExceptionUtil.wrapWithAuthenticationException(e);
		}

	}


	public List<String> getExpectedRegistrationExtensionIds() {
		return expectedRegistrationExtensionIds;
	}

	public void setExpectedRegistrationExtensionIds(List<String> expectedRegistrationExtensionIds) {
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}


	WebAuthnRegistrationContext createRegistrationContext(WebAuthnRegistrationRequest webAuthnRegistrationRequest) {

		byte[] clientDataBytes = Base64UrlUtil.decode(webAuthnRegistrationRequest.getClientDataBase64url());
		byte[] attestationObjectBytes = Base64UrlUtil.decode(webAuthnRegistrationRequest.getAttestationObjectBase64url());
		Set<String> transports = webAuthnRegistrationRequest.getTransports();
		String clientExtensionsJSON = webAuthnRegistrationRequest.getClientExtensionsJSON();
		ServerProperty serverProperty = WebAuthn4JUtil.convertToServerProperty(webAuthnRegistrationRequest.getServerProperty());

		return new WebAuthnRegistrationContext(
				clientDataBytes,
				attestationObjectBytes,
				transports,
				clientExtensionsJSON,
				serverProperty,
				false,
				false,
				expectedRegistrationExtensionIds);
	}

}
