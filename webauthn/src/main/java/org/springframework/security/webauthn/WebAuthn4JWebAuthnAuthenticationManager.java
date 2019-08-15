package org.springframework.security.webauthn;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.WebAuthnRegistrationContext;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.util.exception.WebAuthnException;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.request.WebAuthnAuthenticationRequest;
import org.springframework.security.webauthn.request.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.server.WebAuthnOrigin;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.util.ExceptionUtil;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

public class WebAuthn4JWebAuthnAuthenticationManager implements WebAuthnAuthenticationManager {

	// ~ Instance fields
	// ================================================================================================
	private WebAuthnRegistrationContextValidator registrationContextValidator;
	private WebAuthnAuthenticationContextValidator authenticationContextValidator;

	private CborConverter cborConverter;

	public WebAuthn4JWebAuthnAuthenticationManager(
			WebAuthnRegistrationContextValidator registrationContextValidator,
			WebAuthnAuthenticationContextValidator authenticationContextValidator,
			CborConverter cborConverter) {
		this.registrationContextValidator = registrationContextValidator;
		this.authenticationContextValidator = authenticationContextValidator;
		this.cborConverter = cborConverter;
	}

	public void verifyRegistrationRequest(
			WebAuthnRegistrationRequest webAuthnRegistrationRequest
	) {

		Assert.hasText(webAuthnRegistrationRequest.getClientDataBase64url(), "clientDataBase64url must have text");
		Assert.hasText(webAuthnRegistrationRequest.getAttestationObjectBase64url(), "attestationObjectBase64url must have text");
		if (webAuthnRegistrationRequest.getTransports() != null) {
			webAuthnRegistrationRequest.getTransports().forEach(transport -> Assert.hasText(transport, "each transport must have text"));
		}
		Assert.notNull(webAuthnRegistrationRequest.getServerProperty(), "serverProperty must not be null");

		WebAuthnRegistrationContext registrationContext = createRegistrationContext(webAuthnRegistrationRequest);

		try {
			registrationContextValidator.validate(registrationContext);
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

	private WebAuthnRegistrationContext createRegistrationContext(WebAuthnRegistrationRequest webAuthnRegistrationRequest) {

		byte[] clientDataBytes = Base64UrlUtil.decode(webAuthnRegistrationRequest.getClientDataBase64url());
		byte[] attestationObjectBytes = Base64UrlUtil.decode(webAuthnRegistrationRequest.getAttestationObjectBase64url());
		Set<String> transports = webAuthnRegistrationRequest.getTransports();
		String clientExtensionsJSON = webAuthnRegistrationRequest.getClientExtensionsJSON();
		ServerProperty serverProperty = convertToServerProperty(webAuthnRegistrationRequest.getServerProperty());

		return new WebAuthnRegistrationContext(
				clientDataBytes,
				attestationObjectBytes,
				transports,
				clientExtensionsJSON,
				serverProperty,
				false,
				false,
				webAuthnRegistrationRequest.getExpectedRegistrationExtensionIds());
	}

	private WebAuthnAuthenticationContext createWebAuthnAuthenticationContext(WebAuthnAuthenticationRequest webAuthnAuthenticationRequest) {

		ServerProperty serverProperty = convertToServerProperty(webAuthnAuthenticationRequest.getServerProperty());

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

	private Origin convertToOrigin(WebAuthnOrigin webAuthnOrigin) {
		return new Origin(webAuthnOrigin.getScheme(), webAuthnOrigin.getHost(), webAuthnOrigin.getPort());
	}

	private ServerProperty convertToServerProperty(WebAuthnServerProperty webAuthnServerProperty) {
		return new ServerProperty(
				convertToOrigin(webAuthnServerProperty.getOrigin()),
				webAuthnServerProperty.getRpId(),
				new DefaultChallenge(webAuthnServerProperty.getChallenge().getValue()),
				webAuthnServerProperty.getTokenBindingId());
	}

}
