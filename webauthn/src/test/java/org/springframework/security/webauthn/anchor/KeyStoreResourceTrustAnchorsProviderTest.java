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

package org.springframework.security.webauthn.anchor;

import com.webauthn4j.anchor.KeyStoreException;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.security.cert.TrustAnchor;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class KeyStoreResourceTrustAnchorsProviderTest {

	private KeyStoreResourceTrustAnchorsProvider target;

	@Test
	public void constructor_test() {
		Resource resource = new ClassPathResource("org/springframework/security/webauthn/anchor/KeyStoreResourceTrustAnchorProviderImplTest/test.jks");
		KeyStoreResourceTrustAnchorsProvider target = new KeyStoreResourceTrustAnchorsProvider(resource);
		target.setPassword("password");

		Map<AAGUID, Set<TrustAnchor>> trustAnchors = target.provide();
		assertThat(trustAnchors).isNotEmpty();
	}

	@Test
	public void provide_test() {
		target = new KeyStoreResourceTrustAnchorsProvider();
		Resource resource = new ClassPathResource("org/springframework/security/webauthn/anchor/KeyStoreResourceTrustAnchorProviderImplTest/test.jks");
		target.setKeyStore(resource);
		target.setPassword("password");

		Map<AAGUID, Set<TrustAnchor>> trustAnchors = target.provide();
		assertThat(trustAnchors).isNotEmpty();
	}

	@Test(expected = KeyStoreException.class)
	public void provide_test_with_invalid_path() {
		target = new KeyStoreResourceTrustAnchorsProvider();
		Resource resource = new ClassPathResource("invalid.path.to.jks");
		target.setKeyStore(resource);
		target.setPassword("password");

		target.provide();
	}

	@Test(expected = IllegalArgumentException.class)
	public void afterPropertiesSet_with_invalid_config_test() {
		target = new KeyStoreResourceTrustAnchorsProvider();
		target.afterPropertiesSet();
	}

	@Test
	public void afterPropertiesSet_test() {
		target = new KeyStoreResourceTrustAnchorsProvider();
		Resource resource = new ClassPathResource("org/springframework/security/webauthn/anchor/KeyStoreResourceTrustAnchorProviderImplTest/test.jks");
		target.setKeyStore(resource);
		target.setPassword("password");

		assertThatCode(() -> {
			target.afterPropertiesSet();
		}).doesNotThrowAnyException();
	}
}
