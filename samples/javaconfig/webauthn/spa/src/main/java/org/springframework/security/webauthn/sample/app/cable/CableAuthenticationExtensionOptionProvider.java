package org.springframework.security.webauthn.sample.app.cable;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientInput;
import com.webauthn4j.util.*;
import com.webauthn4j.util.exception.UnexpectedCheckedException;
import com.webauthn4j.util.exception.WebAuthnException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.exception.WebAuthnAuthenticationException;
import org.springframework.security.webauthn.options.ExtensionOptionProvider;
import org.springframework.security.webauthn.sample.domain.entity.AuthenticatorEntity;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetails;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;

import javax.crypto.KeyAgreement;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;

public class CableAuthenticationExtensionOptionProvider implements ExtensionOptionProvider<AuthenticationExtensionClientInput> {

	private static SecureRandom secureRandom = new SecureRandom();

	private static final byte[] HMAC_TAG_CLIENT_EID =
			"client".getBytes(StandardCharsets.UTF_8);
	private static final byte[] HMAC_TAG_AUTHENTICATOR_EID =
			"authenticator".getBytes(StandardCharsets.UTF_8);
	private static final byte[] HKDF_INFO_SESSION_PRE_KEY =
			"FIDO caBLE v1 sessionPreKey".getBytes(StandardCharsets.UTF_8);

	private static int HKDF_SHA_LENGTH = 64;
	private static int K_LENGTH = 32;

	private WebAuthnUserDetailsService webAuthnUserDetailsService;

	public CableAuthenticationExtensionOptionProvider(WebAuthnUserDetailsService webAuthnUserDetailsService) {
		this.webAuthnUserDetailsService = webAuthnUserDetailsService;
	}

	@Override
	public AuthenticationExtensionClientInput provide(HttpServletRequest request) {

		byte[] credentialId = null;
		WebAuthnUserDetails webAuthnUserDetails = webAuthnUserDetailsService.loadUserByCredentialId(credentialId);
		Authenticator authenticator =
				webAuthnUserDetails.getAuthenticators()
						.stream()
						.filter(entry -> Arrays.equals(entry.getAttestedCredentialData().getCredentialId(), credentialId))
						.findFirst().orElseThrow(()-> new AuthenticationServiceException("WebAuthnUserDetails does not contain authenticator with specified credentialId."));

		if (!(authenticator instanceof AuthenticatorEntity)) {
			return null;
		} else {
			AuthenticatorEntity authenticatorEntity = (AuthenticatorEntity) authenticator;
			KeyPair cableKayPair = authenticatorEntity.getCableKayPair();

			CableRegistrationData cableRegistrationData = null;
			byte[] rpPublicKey = null;

			Long version = cableRegistrationData.getVersions().get(0);
			return new CableAuthenticationExtensionClientInput(createCableAuthenticationData(version, cableKayPair, rpPublicKey));
		}
	}

	@Override
	public String getIdentifier() {
		return CableAuthenticationExtensionClientInput.ID;
	}

	private CableAuthenticationData createCableAuthenticationData(Long version, KeyPair cableKeyPair, byte[] rpPublicKey){

		byte[] nonce = new byte[8];
		secureRandom.nextBytes(nonce);

		byte[] sharedSecret = getSharedSecret(cableKeyPair.getPrivate(), rpPublicKey);

		byte[] info = "FIDO caBLE v1 pairing data".getBytes(StandardCharsets.US_ASCII);
		byte[] versionBytes = UnsignedNumberUtil.toBytes(version);
		byte[] uncompressedPublicKey = ECUtil.createUncompressedPublicKey((ECPublicKey) cableKeyPair.getPublic());

		byte[] digest = MessageDigestUtil.createSHA256().digest(
				ByteBuffer.allocate(versionBytes.length + uncompressedPublicKey.length + rpPublicKey.length)
						.put(versionBytes).put(uncompressedPublicKey).put(rpPublicKey).array());
		
		byte[] result = HKDFUtil.calculateHKDFSHA256(sharedSecret, digest, info, HKDF_SHA_LENGTH);

		byte[] irk = Arrays.copyOf(result, K_LENGTH);
		byte[] lk = Arrays.copyOfRange(result, K_LENGTH, 2 * K_LENGTH);

		byte[] clientEidHash = Arrays.copyOf(
				MACUtil.calculateHmacSHA256(irk, ByteBuffer.allocate(nonce.length + HMAC_TAG_CLIENT_EID.length).put(nonce).put(HMAC_TAG_CLIENT_EID).array()),
				8);
		byte[] clientEid = ByteBuffer.allocate(nonce.length + clientEidHash.length).put(nonce).put(clientEidHash).array();

		byte[] authenticatorEid = Arrays.copyOf(MACUtil.calculateHmacSHA256(irk,
				ByteBuffer.allocate(clientEid.length + HMAC_TAG_AUTHENTICATOR_EID.length).put(clientEid).put(HMAC_TAG_AUTHENTICATOR_EID).array()),
				16);

		byte[] sessionPreKey = HKDFUtil.calculateHKDFSHA256(lk, nonce, HKDF_INFO_SESSION_PRE_KEY, 32);

		return new CableAuthenticationData(version, clientEid, authenticatorEid, sessionPreKey);
	}

	private static byte[] getSharedSecret(PrivateKey privateKey, byte[] publicKey) {
		try {
			KeyAgreement agreement = KeyAgreement.getInstance("ECDH");
			agreement.init(privateKey);
			agreement.doPhase(decodePublicKey(publicKey), true);
			return agreement.generateSecret();
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e) {
			throw new UnexpectedCheckedException(e);
		}
	}

	private static PublicKey decodePublicKey(byte[] encodedPublicKey) {
		return decodePublicKey(Arrays.copyOfRange(encodedPublicKey, 1, 1 + 32),
				Arrays.copyOfRange(encodedPublicKey, 1 + 32, encodedPublicKey.length));
	}

	private static PublicKey decodePublicKey(byte[] x, byte[] y) {
		try {
			ECParameterSpec curve = ECUtil.P_256_SPEC;
			byte[] encodedPublicKey = ByteBuffer.allocate(1 + x.length + y.length).put(new byte[] {0x04}).put(x).put(y).array();
			ECPoint point = createECPoint(encodedPublicKey);
			return KeyFactory.getInstance("ECDSA").generatePublic(new ECPublicKeySpec(point, curve));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new UnexpectedCheckedException(e);
		}
	}

	private static ECPoint createECPoint(byte[] publicKey){
		if (publicKey.length != 65) {
			throw new IllegalArgumentException("publicKey must be 65 bytes length");
		}
		byte[] x = Arrays.copyOfRange(publicKey, 1, 1 + 32);
		byte[] y = Arrays.copyOfRange(publicKey, 1 + 32, 1 + 32 + 32);
		return new ECPoint(
				new BigInteger(1, x),
				new BigInteger(1, y)
		);
	}
}
