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

package org.springframework.security.webauthn.converter;

import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.client.CollectedClientData;
import org.springframework.core.convert.converter.Converter;

/**
 * Spring converter which converts from Base64Url {@link String} to {@link CollectedClientData}
 *
 * @author Yoshikazu Nojima
 */
public class Base64UrlStringToCollectedClientDataConverter implements Converter<String, CollectedClientData> {

	//~ Instance fields
	// ================================================================================================
	private CollectedClientDataConverter converter;

	// ~ Constructor
	// ========================================================================================================

	public Base64UrlStringToCollectedClientDataConverter(JsonConverter jsonConverter) {
		converter = new CollectedClientDataConverter(jsonConverter);
	}

	/**
	 * Convert Base64Url {@link String} to {@link CollectedClientData}
	 *
	 * @param source base64String
	 * @return collectedClientData
	 */
	@Override
	public CollectedClientData convert(String source) {
		return converter.convert(source);
	}
}
