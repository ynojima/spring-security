package org.springframework.security.webauthn.authenticator;

import java.util.Objects;

public class WebAuthnAuthenticatorImpl implements WebAuthnAuthenticator {

	// ~ Instance fields
	// ================================================================================================
	private String name;
	private byte[] attestedCredentialData;
	private byte[] attestationStatement;
	private long counter;

	// ~ Constructor
	// ========================================================================================================

	/**
	 * Constructor
	 *
	 * @param name                   authenticator's friendly name
	 * @param attestedCredentialData attested credential data
	 * @param attestationStatement   attestation statement
	 * @param counter                counter
	 */
	public WebAuthnAuthenticatorImpl(String name, byte[] attestedCredentialData, byte[] attestationStatement, long counter) {
		this.name = name;
		this.attestedCredentialData = attestedCredentialData;
		this.attestationStatement = attestationStatement;
		this.counter = counter;
	}

	// ~ Methods
	// ========================================================================================================

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getAttestedCredentialData() {
		return attestedCredentialData;
	}

	public void setAttestedCredentialData(byte[] attestedCredentialData) {
		this.attestedCredentialData = attestedCredentialData;
	}

	public byte[] getAttestationStatement() {
		return attestationStatement;
	}

	public void setAttestationStatement(byte[] attestationStatement) {
		this.attestationStatement = attestationStatement;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		WebAuthnAuthenticatorImpl that = (WebAuthnAuthenticatorImpl) o;
		return Objects.equals(name, that.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), name);
	}
}
