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

import org.springframework.security.webauthn.authenticator.AuthenticatorTransport;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;

import java.util.Set;

/**
 * Authenticator model
 */
public class AuthenticatorEntity implements WebAuthnAuthenticator {

	private Integer id;
	private byte[] credentialId;
	private String name;
	private UserEntity user;
	private long counter;
	private Set<AuthenticatorTransport> transports;
	private byte[] attestationObject;
	private String clientExtensions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public byte[] getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(byte[] credentialId){
		this.credentialId = credentialId;
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
	public Set<AuthenticatorTransport> getTransports() {
		return transports;
	}

	public void setTransports(Set<AuthenticatorTransport> transports) {
		this.transports = transports;
	}

	@Override
	public byte[] getAttestationObject() {
		return attestationObject;
	}

	@Override
	public void setAttestationObject(byte[] attestationObject) {
		this.attestationObject = attestationObject;
	}

	@Override
	public String getClientExtensions() {
		return clientExtensions;
	}

	public void setClientExtensions(String clientExtensions) {
		this.clientExtensions = clientExtensions;
	}
}
