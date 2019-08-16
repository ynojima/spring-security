package org.springframework.security.webauthn.authenticator;


import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class  WebAuthnAuthenticatorImpl implements WebAuthnAuthenticator {

	// ~ Instance fields
	// ================================================================================================
	private String name;
	private byte[] attestationObject;
	private long counter;
	private String clientExtensions;

	// ~ Constructor
	// ========================================================================================================

	/**
	 * Constructor
	 *
	 * @param name                   authenticator's friendly name
	 * @param attestationObject   attestation object
	 * @param counter                counter
	 */
	public WebAuthnAuthenticatorImpl(String name, byte[] attestationObject, long counter) {
		this.name = name;
		this.attestationObject = attestationObject;
		this.counter = counter;
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public byte[] getCredentialId() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public byte[] getAttestationObject() {
		return attestationObject;
	}

	@Override
	public void setAttestationObject(byte[] attestationObject) {
		this.attestationObject = attestationObject;
	}

	public long getCounter() {
		return counter;
	}

	@Override
	public void setCounter(long counter) {
		this.counter = counter;
	}

	@Override
	public Set<AuthenticatorTransport> getTransports() {
		return null;
	}

	@Override
	public String getClientExtensions() {
		return clientExtensions;
	}

	public void setClientExtensions(String clientExtensions) {
		this.clientExtensions = clientExtensions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WebAuthnAuthenticatorImpl that = (WebAuthnAuthenticatorImpl) o;
		return counter == that.counter &&
				Objects.equals(name, that.name) &&
				Arrays.equals(attestationObject, that.attestationObject) &&
				Objects.equals(clientExtensions, that.clientExtensions);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name, counter, clientExtensions);
		result = 31 * result + Arrays.hashCode(attestationObject);
		return result;
	}
}
