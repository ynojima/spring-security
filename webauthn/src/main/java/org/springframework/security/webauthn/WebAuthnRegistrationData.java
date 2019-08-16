package org.springframework.security.webauthn;

import org.springframework.security.webauthn.server.WebAuthnServerProperty;

import java.util.List;
import java.util.Set;

public class WebAuthnRegistrationData {

	private final String clientDataBase64url;
	private final String attestationObjectBase64url;
	private final Set<String> transports;
	private final String clientExtensionsJSON;

	private final WebAuthnServerProperty serverProperty;
	private final List<String> expectedRegistrationExtensionIds;

	public WebAuthnRegistrationData(String clientDataBase64url, String attestationObjectBase64url, Set<String> transports, String clientExtensionsJSON,
									WebAuthnServerProperty serverProperty,
									List<String> expectedRegistrationExtensionIds) {
		this.clientDataBase64url = clientDataBase64url;
		this.attestationObjectBase64url = attestationObjectBase64url;
		this.transports = transports;
		this.clientExtensionsJSON = clientExtensionsJSON;
		this.serverProperty = serverProperty;
		this.expectedRegistrationExtensionIds = expectedRegistrationExtensionIds;
	}

	public String getClientDataBase64url() {
		return clientDataBase64url;
	}

	public String getAttestationObjectBase64url() {
		return attestationObjectBase64url;
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
