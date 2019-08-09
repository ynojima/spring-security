package org.springframework.security.webauthn.util;

import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import org.springframework.security.webauthn.server.WebAuthnOrigin;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;

public class WebAuthn4JUtil {

	private WebAuthn4JUtil(){}

	public static Origin convertToOrigin(WebAuthnOrigin webAuthnOrigin) {
		return new Origin(webAuthnOrigin.getScheme(), webAuthnOrigin.getHost(), webAuthnOrigin.getPort());
	}

	public static ServerProperty convertToServerProperty(WebAuthnServerProperty webAuthnServerProperty) {
		return new ServerProperty(
				convertToOrigin(webAuthnServerProperty.getOrigin()),
				webAuthnServerProperty.getRpId(),
				new DefaultChallenge(webAuthnServerProperty.getChallenge().getValue()),
				webAuthnServerProperty.getTokenBindingId());
	}

}
