package org.springframework.security.webauthn;


import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.AuthenticatorDataConverter;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.client.ClientDataType;
import com.webauthn4j.data.client.CollectedClientData;
import com.webauthn4j.test.TestDataUtil;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.webauthn.challenge.WebAuthnChallenge;
import org.springframework.security.webauthn.challenge.WebAuthnChallengeImpl;
import org.springframework.security.webauthn.exception.BadAttestationStatementException;
import org.springframework.security.webauthn.server.WebAuthnOrigin;
import org.springframework.security.webauthn.server.WebAuthnServerProperty;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class WebAuthn4JWebAuthnAuthenticationManagerTest {

	private WebAuthnRegistrationContextValidator registrationContextValidator = mock(WebAuthnRegistrationContextValidator.class);
	private WebAuthnAuthenticationContextValidator authenticationContextValidator = mock(WebAuthnAuthenticationContextValidator.class);
	private CborConverter cborConverter = new CborConverter();
	private WebAuthnAuthenticationManager target = new WebAuthn4JWebAuthnAuthenticationManager(registrationContextValidator, authenticationContextValidator, cborConverter);

	@Test(expected = BadAttestationStatementException.class)
	public void verifyRegistrationData_caught_exception_test() {

		doThrow(new com.webauthn4j.validator.exception.BadAttestationStatementException("dummy"))
				.when(registrationContextValidator).validate(any());

		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.setScheme("https");
		mockHttpServletRequest.setServerName("example.com");
		mockHttpServletRequest.setServerPort(443);

		byte[] clientDataJSON = new byte[0]; //dummy
		byte[] attestationObject = new byte[0]; //dummy
		WebAuthnServerProperty serverProperty = new WebAuthnServerProperty(
				new WebAuthnOrigin("https://example.com"),
				"example.com",
				new WebAuthnChallengeImpl(),
				new byte[]{0x43, 0x21}
		);

		target.verifyRegistrationData(new WebAuthnRegistrationData(clientDataJSON, attestationObject, null, null, serverProperty, null));

	}

}
