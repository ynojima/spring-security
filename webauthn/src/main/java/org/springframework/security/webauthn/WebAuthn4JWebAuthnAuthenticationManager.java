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
import org.springframework.security.webauthn.server.WebAuthnOrigin;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;
import org.springframework.security.webauthn.util.ExceptionUtil;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class WebAuthn4JWebAuthnAuthenticationManager implements WebAuthnAuthenticationManager {

	// ~ Instance fields
	// ================================================================================================
	private WebAuthnRegistrationContextValidator registrationContextValidator;
	private WebAuthnAuthenticationContextValidator authenticationContextValidator;

	private String rpId;

	private CborConverter cborConverter;

	public WebAuthn4JWebAuthnAuthenticationManager(
			WebAuthnRegistrationContextValidator registrationContextValidator,
			WebAuthnAuthenticationContextValidator authenticationContextValidator,
			CborConverter cborConverter) {
		this.registrationContextValidator = registrationContextValidator;
		this.authenticationContextValidator = authenticationContextValidator;
		this.cborConverter = cborConverter;
	}

	public void verifyRegistrationData(
			WebAuthnRegistrationData registrationData
	) {

		Assert.hasText(registrationData.getClientDataBase64url(), "clientDataBase64url must have text");
		Assert.hasText(registrationData.getAttestationObjectBase64url(), "attestationObjectBase64url must have text");
		if (registrationData.getTransports() != null) {
			registrationData.getTransports().forEach(transport -> Assert.hasText(transport, "each transport must have text"));
		}
		Assert.notNull(registrationData.getServerProperty(), "serverProperty must not be null");

		WebAuthnRegistrationContext registrationContext = createRegistrationContext(registrationData);

		try {
			registrationContextValidator.validate(registrationContext);
		} catch (WebAuthnException e) {
			throw ExceptionUtil.wrapWithAuthenticationException(e);
		}
	}

	@Override
	public void verifyAuthenticationData(WebAuthnAuthenticationData authenticationData, WebAuthnAuthenticator webAuthnAuthenticator) {

		//TODO: null check

		WebAuthnAuthenticationContext authenticationContext = createWebAuthnAuthenticationContext(authenticationData);

		AttestationObject attestationObject = cborConverter.readValue(webAuthnAuthenticator.getAttestationObject(), AttestationObject.class);

		Set<AuthenticatorTransport> transports;
		if (webAuthnAuthenticator.getTransports() == null) {
			transports = Collections.emptySet();
		}
		else {
			transports = webAuthnAuthenticator.getTransports().stream()
					.map(transport -> AuthenticatorTransport.create(transport.getValue()))
					.collect(Collectors.toSet());
		}

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

	@Override
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

	public String getRpId() {
		return rpId;
	}

	public void setRpId(String rpId) {
		this.rpId = rpId;
	}

	private WebAuthnRegistrationContext createRegistrationContext(WebAuthnRegistrationData webAuthnRegistrationData) {

		byte[] clientDataBytes = Base64UrlUtil.decode(webAuthnRegistrationData.getClientDataBase64url());
		byte[] attestationObjectBytes = Base64UrlUtil.decode(webAuthnRegistrationData.getAttestationObjectBase64url());
		Set<String> transports = webAuthnRegistrationData.getTransports();
		String clientExtensionsJSON = webAuthnRegistrationData.getClientExtensionsJSON();
		ServerProperty serverProperty = convertToServerProperty(webAuthnRegistrationData.getServerProperty());

		return new WebAuthnRegistrationContext(
				clientDataBytes,
				attestationObjectBytes,
				transports,
				clientExtensionsJSON,
				serverProperty,
				false,
				false,
				webAuthnRegistrationData.getExpectedRegistrationExtensionIds());
	}

	private WebAuthnAuthenticationContext createWebAuthnAuthenticationContext(WebAuthnAuthenticationData webAuthnAuthenticationData) {

		ServerProperty serverProperty = convertToServerProperty(webAuthnAuthenticationData.getServerProperty());

		return new WebAuthnAuthenticationContext(
				webAuthnAuthenticationData.getCredentialId(),
				webAuthnAuthenticationData.getClientDataJSON(),
				webAuthnAuthenticationData.getAuthenticatorData(),
				webAuthnAuthenticationData.getSignature(),
				webAuthnAuthenticationData.getClientExtensionsJSON(),
				serverProperty,
				webAuthnAuthenticationData.isUserVerificationRequired(),
				webAuthnAuthenticationData.isUserPresenceRequired(),
				webAuthnAuthenticationData.getExpectedAuthenticationExtensionIds()
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
