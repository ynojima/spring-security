package org.springframework.security.webauthn.challenge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.webauthn4j.util.ArrayUtil;
import com.webauthn4j.util.AssertUtil;
import com.webauthn4j.util.Base64UrlUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class WebAuthnChallengeImpl implements WebAuthnChallenge {
	private final byte[] value;

	/**
	 * Creates a new instance
	 *
	 * @param value the value of the challenge
	 */
	public WebAuthnChallengeImpl(byte[] value) {
		AssertUtil.notNull(value, "value cannot be null");
		this.value = value;
	}

	public WebAuthnChallengeImpl(String base64urlString) {
		AssertUtil.notNull(base64urlString, "base64urlString cannot be null");
		this.value = Base64UrlUtil.decode(base64urlString);
	}

	public WebAuthnChallengeImpl() {
		UUID uuid = UUID.randomUUID();
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		this.value = ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
	}

	@JsonCreator
	public WebAuthnChallengeImpl create(String base64url){
		return new WebAuthnChallengeImpl(Base64UrlUtil.decode(base64url));
	}

	@JsonValue
	public String toBase64UrlString(){
		return Base64UrlUtil.encodeToString(getValue());
	}

	@Override
	public byte[] getValue() {
		return ArrayUtil.clone(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WebAuthnChallengeImpl that = (WebAuthnChallengeImpl) o;
		return Arrays.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}
}
