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

import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.util.CborConverter;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.test.TestDataUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64UrlStringToAttestationObjectConverterTest {

	private CborConverter cborConverter = new CborConverter();

	@Test
	public void convert_test() {
		AttestationObject expected = TestDataUtil.createAttestationObjectWithFIDOU2FAttestationStatement();
		String source = new AttestationObjectConverter(cborConverter).convertToBase64urlString(expected);
		Base64UrlStringToAttestationObjectConverter converter = new Base64UrlStringToAttestationObjectConverter(cborConverter);
		AttestationObject result = converter.convert(source);
		assertThat(result).isEqualTo(expected);
	}
}
