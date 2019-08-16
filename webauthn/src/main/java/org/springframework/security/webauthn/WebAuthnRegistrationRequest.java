package org.springframework.security.webauthn;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Set;

public class WebAuthnRegistrationRequest {

	private HttpServletRequest httpServletRequest;
	private String clientDataBase64url;
	private String attestationObjectBase64url;
	private Set<String> transports;
	private String clientExtensionsJSON;

	public WebAuthnRegistrationRequest(
			HttpServletRequest httpServletRequest,
			String clientDataBase64url,
			String attestationObjectBase64url,
			Set<String> transports,
			String clientExtensionsJSON) {
		this.httpServletRequest = httpServletRequest;
		this.clientDataBase64url = clientDataBase64url;
		this.attestationObjectBase64url = attestationObjectBase64url;
		this.transports = transports;
		this.clientExtensionsJSON = clientExtensionsJSON;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WebAuthnRegistrationRequest that = (WebAuthnRegistrationRequest) o;
		return Objects.equals(httpServletRequest, that.httpServletRequest) &&
				Objects.equals(clientDataBase64url, that.clientDataBase64url) &&
				Objects.equals(attestationObjectBase64url, that.attestationObjectBase64url) &&
				Objects.equals(transports, that.transports) &&
				Objects.equals(clientExtensionsJSON, that.clientExtensionsJSON);
	}

	@Override
	public int hashCode() {
		return Objects.hash(httpServletRequest, clientDataBase64url, attestationObjectBase64url, transports, clientExtensionsJSON);
	}
}
