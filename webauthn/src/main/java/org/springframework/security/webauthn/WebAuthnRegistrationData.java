package org.springframework.security.webauthn;

import org.springframework.security.webauthn.server.WebAuthnServerProperty;

import java.util.List;
import java.util.Set;

public class WebAuthnRegistrationData {

	private final byte[] clientDataJSON;
	private final byte[] attestationObject;
	private final Set<String> transports;
	private final String clientExtensionsJSON;

	private final WebAuthnServerProperty serverProperty;
	private final List<String> expectedRegistrationExtensionIds;

	public WebAuthnRegistrationData(byte[] clientDataJSON, byte[] attestationObject, Set<String> transports, String clientExtensionsJSON,
									WebAuthnServerProperty serverProperty,
									List<String> expectedRegistrationExtensionIds) {
		this.clientDataJSON = clientDataJSON;
		this.attestationObject = attestationObject;
		this.transports = transports;
		this.clientExtensionsJSON = clientExtensionsJSON;
		this.serverProperty = serverProperty;
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}

	public byte[] getClientDataJSON() {
		return clientDataJSON;
	}

	public byte[] getAttestationObject() {
		return attestationObject;
	}

	public Set<String> getTransports() {
		return transports;
	}

	public String getClientExtensionsJSON() {
		return clientExtensionsJSON;
	}

	public WebAuthnServerProperty getServerProperty() {
		return serverProperty;
	}

	public List<String> getExpectedRegistrationExtensionIds() {
		return expectedRegistrationExtensionIds;
	}
}
