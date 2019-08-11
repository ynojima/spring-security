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

package org.springframework.security.webauthn.sample.domain.entity;

import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;

import java.util.Map;
import java.util.Set;

/**
 * Authenticator model
 */
public class AuthenticatorEntity implements WebAuthnAuthenticator {

	private Integer id;

	private String name;

	private UserEntity user;

	private long counter;

	private Set<AuthenticatorTransport> transports;

	private byte[] attestedCredentialData;

	private byte[] attestationStatement;

	private Map<String, RegistrationExtensionClientOutput> clientExtensions;

	private Map<String, RegistrationExtensionAuthenticatorOutput> authenticatorExtensions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	@Override
	public byte[] getCredentialId() {
		return new byte[0];
	}

	@Override
	public Set<AuthenticatorTransport> getTransports() {
		return transports;
	}

	public void setTransports(Set<AuthenticatorTransport> transports) {
		this.transports = transports;
	}

	public byte[] getAttestedCredentialData() {
		return attestedCredentialData;
	}

	public void setAttestedCredentialData(byte[] attestedCredentialData) {
		this.attestedCredentialData = attestedCredentialData;
	}

	public byte[] getAttestationStatement() {
		return attestationStatement;
	}

	public void setAttestationStatement(byte[] attestationStatement) {
		this.attestationStatement = attestationStatement;
	}

	@Override
	public Map<String, RegistrationExtensionClientOutput> getClientExtensions() {
		return clientExtensions;
	}

	@Override
	public void setClientExtensions(Map<String, RegistrationExtensionClientOutput> clientExtensions) {
		this.clientExtensions = clientExtensions;
	}

	@Override
	public Map<String, RegistrationExtensionAuthenticatorOutput> getAuthenticatorExtensions() {
		return authenticatorExtensions;
	}

	@Override
	public void setAuthenticatorExtensions(Map<String, RegistrationExtensionAuthenticatorOutput> authenticatorExtensions) {
		this.authenticatorExtensions = authenticatorExtensions;
	}
}
