/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.webauthn.sample.app.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.webauthn.converter.Base64UrlStringToCollectedClientDataConverter;
import org.springframework.security.webauthn.sample.app.api.CollectedClientDataForm;

import java.io.IOException;

/**
 * Jackson Deserializer for {@link CollectedClientDataForm}
 */
@JsonComponent
public class CollectedClientDataDeserializer extends StdDeserializer<CollectedClientDataForm> {

	private Base64UrlStringToCollectedClientDataConverter base64UrlStringToCollectedClientDataConverter;

	public CollectedClientDataDeserializer(Base64UrlStringToCollectedClientDataConverter base64UrlStringToCollectedClientDataConverter) {
		super(CollectedClientDataForm.class);
		this.base64UrlStringToCollectedClientDataConverter = base64UrlStringToCollectedClientDataConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectedClientDataForm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = p.getValueAsString();
		CollectedClientDataForm result = new CollectedClientDataForm();
		result.setCollectedClientData(base64UrlStringToCollectedClientDataConverter.convert(value));
		result.setClientDataBase64(value);
		return result;
	}
}
