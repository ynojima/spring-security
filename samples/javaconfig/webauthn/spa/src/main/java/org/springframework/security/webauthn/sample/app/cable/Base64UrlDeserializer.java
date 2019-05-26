package org.springframework.security.webauthn.sample.app.cable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webauthn4j.util.Base64UrlUtil;

import java.io.IOException;

public class Base64UrlDeserializer extends StdDeserializer<byte[]> {

	protected Base64UrlDeserializer() {
		super(byte[].class);
	}

	@Override
	public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return Base64UrlUtil.decode(p.getValueAsString());
	}
}
