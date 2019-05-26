package org.springframework.security.webauthn.sample.app.cable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webauthn4j.util.Base64UrlUtil;

import java.io.IOException;

public class Base64UrlSerializer extends StdSerializer<byte[]> {

	protected Base64UrlSerializer() {
		super(byte[].class);
	}

	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(Base64UrlUtil.encodeToString(value));
	}
}
