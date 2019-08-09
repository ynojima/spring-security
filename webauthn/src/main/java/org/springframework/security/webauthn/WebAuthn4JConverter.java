package org.springframework.security.webauthn;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;

class WebAuthn4JConverter {

	private CborConverter cborConverter;

	public WebAuthn4JConverter(CborConverter cborConverter) {
		this.cborConverter = cborConverter;
	}

	Authenticator convertToAuthenticator(WebAuthnAuthenticator webAuthnAuthenticator){
		AttestedCredentialData attestedCredentialData = cborConverter.readValue(webAuthnAuthenticator.getAttestedCredentialData(), AttestedCredentialData.class);
		AttestationStatement attestationStatement = cborConverter.readValue(webAuthnAuthenticator.getAttestationStatement(), AttestationStatement.class);
		long counter = webAuthnAuthenticator.getCounter();
		return new AuthenticatorImpl(attestedCredentialData, attestationStatement, counter);
	}
}
