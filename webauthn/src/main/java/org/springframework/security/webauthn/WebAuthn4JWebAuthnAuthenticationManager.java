package org.springframework.security.webauthn;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.WebAuthnRegistrationContext;
import com.webauthn4j.data.attestation.AttestationObject;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class WebAuthn4JWebAuthnAuthenticationManager implements WebAuthnAuthenticationManager {

	// ~ Instance fields
	// ================================================================================================
	private WebAuthnRegistrationContextValidator registrationContextValidator;
	private WebAuthnAuthenticationContextValidator authenticationContextValidator;

	private CborConverter cborConverter;

	private String rpId; //TODO
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

		Assert.hasText(webAuthnRegistrationRequest.getClientDataBase64url(), "clientDataBase64url must have text");
		Assert.hasText(webAuthnRegistrationRequest.getAttestationObjectBase64url(), "attestationObjectBase64url must have text");
		Assert.notNull(webAuthnRegistrationRequest.getTransports(), "transports must not be null");
		if (webAuthnRegistrationRequest.getTransports() != null) {
			webAuthnRegistrationRequest.getTransports().forEach(transport -> Assert.hasText(transport, "each transport must have text"));
		}
		Assert.hasText(webAuthnRegistrationRequest.getClientExtensionsJSON(), "clientExtensionsJSON must have text");
		Assert.notNull(webAuthnRegistrationRequest.getServerProperty(), "serverProperty must not be null");
		Assert.notNull(webAuthnRegistrationRequest.getExpectedRegistrationExtensionIds(), "expectedRegistrationExtensionIds must not be null");

		WebAuthnRegistrationContext registrationContext = createRegistrationContext(webAuthnRegistrationRequest);

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

		//TODO: null check

		WebAuthnAuthenticationContext authenticationContext = createWebAuthnAuthenticationContext(webAuthnAuthenticationRequest);

		AttestationObject attestationObject = cborConverter.readValue(webAuthnAuthenticator.getAttestationObject(), AttestationObject.class);

		Set<AuthenticatorTransport> transports = webAuthnAuthenticator.getTransports();
		transports = transports == null ? Collections.emptySet() : transports;

		Authenticator authenticator = new AuthenticatorImpl(
				attestationObject.getAuthenticatorData().getAttestedCredentialData(),
				attestationObject.getAttestationStatement(),
				webAuthnAuthenticator.getCounter(),
				transports
				);

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

	private WebAuthnRegistrationContext createRegistrationContext(WebAuthnRegistrationRequest webAuthnRegistrationRequest) {

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

	private WebAuthnAuthenticationContext createWebAuthnAuthenticationContext(WebAuthnAuthenticationRequest webAuthnAuthenticationRequest) {

		ServerProperty serverProperty = WebAuthn4JUtil.convertToServerProperty(webAuthnAuthenticationRequest.getServerProperty());

		return new WebAuthnAuthenticationContext(
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
	}

}
