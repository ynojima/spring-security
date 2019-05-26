package org.springframework.security.webauthn.sample.app.cable;

import com.webauthn4j.data.extension.authenticator.AuthenticationExtensionsAuthenticatorOutputs;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.validator.CustomRegistrationValidator;
import com.webauthn4j.validator.RegistrationObject;

public class CableRegistrationExtensionAuthenticatorOutputValidator implements CustomRegistrationValidator {
	@Override
	public void validate(RegistrationObject registrationObject) {
		AuthenticationExtensionsAuthenticatorOutputs<RegistrationExtensionAuthenticatorOutput> authenticatorExtensions =
				registrationObject.getAttestationObject().getAuthenticatorData().getExtensions();
		RegistrationExtensionAuthenticatorOutput output = authenticatorExtensions.getOrDefault(CableRegistrationExtensionAuthenticatorOutput.ID, null);
		CableRegistrationExtensionAuthenticatorOutput cableRegistrationExtensionAuthenticatorOutput
				= (CableRegistrationExtensionAuthenticatorOutput)output;
		cableRegistrationExtensionAuthenticatorOutput.getIdentifier();
	}
}
