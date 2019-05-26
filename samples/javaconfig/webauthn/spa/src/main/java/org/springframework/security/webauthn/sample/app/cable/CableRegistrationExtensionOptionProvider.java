package org.springframework.security.webauthn.sample.app.cable;

import com.webauthn4j.data.extension.client.RegistrationExtensionClientInput;
import org.springframework.security.webauthn.options.ExtensionOptionProvider;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;

public class CableRegistrationExtensionOptionProvider implements ExtensionOptionProvider<RegistrationExtensionClientInput> {


	private CableKeyPairRepository cableKeyPairRepository;

	public CableRegistrationExtensionOptionProvider(CableKeyPairRepository cableKeyPairRepository) {
		this.cableKeyPairRepository = cableKeyPairRepository;
	}

	@Override
	public RegistrationExtensionClientInput provide(HttpServletRequest request) {
		KeyPair sessionKeyPair = cableKeyPairRepository.loadCableKeyPair(request);
		ECPublicKey publicKey = (ECPublicKey) sessionKeyPair.getPublic();
		CableRegistrationData cableRegistrationData = new CableRegistrationData(publicKey);
		return new CableRegistrationExtensionClientInput(cableRegistrationData);
	}

	@Override
	public String getIdentifier() {
		return CableRegistrationExtensionClientInput.ID;
	}
}
