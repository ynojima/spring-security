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

package org.springframework.security.webauthn.sample.util.modelmapper;

import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import org.modelmapper.AbstractConverter;
import org.springframework.util.Base64Utils;

/**
 * Converter which converts from {@link String} to {@link Challenge}
 */
public class StringToChallengeConverter extends AbstractConverter<String, Challenge> {

	@Override
	protected Challenge convert(String source) {
		byte[] challenge = Base64Utils.decodeFromUrlSafeString(source);
		return new DefaultChallenge(challenge);
	}
}
