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

package org.springframework.security.webauthn.userdetails;

import com.webauthn4j.util.Base64UrlUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticatorService;
import org.springframework.security.webauthn.exception.CredentialIdNotFoundException;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class InMemoryWebAuthnUserDetailsManager implements WebAuthnUserDetailsService, WebAuthnAuthenticatorService {

	private Map<String, WebAuthnUserDetails> users = new HashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WebAuthnUserDetails loadUserByUsername(String username) {
		WebAuthnUserDetails userDetails = users.get(username);
		if(userDetails == null){
			throw new UsernameNotFoundException(String.format("UserEntity with username'%s' is not found.", username));
		}
		return userDetails;
	}

	@Override
	public WebAuthnUserDetails loadUserByCredentialId(byte[] credentialId) throws CredentialIdNotFoundException {
		return users
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().getAuthenticators().stream().anyMatch(authenticator -> Arrays.equals(authenticator.getCredentialId(), credentialId)))
				.findFirst()
				.orElseThrow(() -> new CredentialIdNotFoundException(String.format("AuthenticatorEntity with credentialId'%s' is not found.", Base64UrlUtil.encodeToString(credentialId))))
				.getValue();
	}

	public void createUser(WebAuthnUserDetails user) {
		Assert.isTrue(!userExists(user.getUsername()), "user should not exist");
		users.put(user.getUsername(), user);
	}

	public void deleteUser(String username) {
		users.remove(username);
	}

	public boolean userExists(String username) {
		WebAuthnUserDetails userDetails = users.get(username);
		return userDetails != null;
	}

	@Override
	public void updateCounter(byte[] credentialId, long counter) throws CredentialIdNotFoundException {

		WebAuthnAuthenticator authenticator = users
				.entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().getAuthenticators().stream())
				.filter(entry -> Arrays.equals(entry.getCredentialId(), credentialId))
				.findFirst()
				.orElseThrow(() -> new CredentialIdNotFoundException(String.format("AuthenticatorEntity with credentialId'%s' is not found.", Base64UrlUtil.encodeToString(credentialId))));

		authenticator.setCounter(counter);
	}

}
