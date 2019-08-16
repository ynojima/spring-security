package org.springframework.security.webauthn;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Set;

public class WebAuthnRegistrationRequest {

	private HttpServletRequest httpServletRequest;
	private String clientDataBase64Url;
	private String attestationObjectBase64Url;
	private Set<String> transports;
	private String clientExtensionsJSON;

	public WebAuthnRegistrationRequest(
			HttpServletRequest httpServletRequest,
			String clientDataBase64Url,
			String attestationObjectBase64Url,
			Set<String> transports,
			String clientExtensionsJSON) {
		this.httpServletRequest = httpServletRequest;
		this.clientDataBase64Url = clientDataBase64Url;
		this.attestationObjectBase64Url = attestationObjectBase64Url;
		this.transports = transports;
		this.clientExtensionsJSON = clientExtensionsJSON;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public String getClientDataBase64Url() {
		return clientDataBase64Url;
	}

	public String getAttestationObjectBase64Url() {
		return attestationObjectBase64Url;
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
				Objects.equals(clientDataBase64Url, that.clientDataBase64Url) &&
				Objects.equals(attestationObjectBase64Url, that.attestationObjectBase64Url) &&
				Objects.equals(transports, that.transports) &&
				Objects.equals(clientExtensionsJSON, that.clientExtensionsJSON);
	}

	@Override
	public int hashCode() {
		return Objects.hash(httpServletRequest, clientDataBase64Url, attestationObjectBase64Url, transports, clientExtensionsJSON);
	}
}
