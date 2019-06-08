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

package org.springframework.security.webauthn.sample.domain.component;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.util.Base64UrlUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.webauthn.exception.CredentialIdNotFoundException;
import org.springframework.security.webauthn.sample.domain.constant.MessageCodes;
import org.springframework.security.webauthn.sample.domain.entity.AuthenticatorEntity;
import org.springframework.security.webauthn.sample.domain.entity.UserEntity;
import org.springframework.security.webauthn.sample.domain.exception.WebAuthnSampleBusinessException;
import org.springframework.security.webauthn.sample.domain.exception.WebAuthnSampleEntityNotFoundException;
import org.springframework.security.webauthn.sample.domain.repository.AuthenticatorEntityRepository;
import org.springframework.security.webauthn.sample.domain.repository.UserEntityRepository;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetails;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * {@inheritDoc}
 */
@Component
@Transactional
public class UserManagerImpl implements UserManager, WebAuthnUserDetailsService {

	private ModelMapper modelMapper;

	private UserEntityRepository userEntityRepository;
	private AuthenticatorEntityRepository authenticatorEntityRepository;

	@Autowired
	public UserManagerImpl(ModelMapper mapper, UserEntityRepository userEntityRepository, AuthenticatorEntityRepository authenticatorEntityRepository) {
		this.modelMapper = mapper;
		this.userEntityRepository = userEntityRepository;
		this.authenticatorEntityRepository = authenticatorEntityRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserEntity findById(int id) {
		return userEntityRepository.findById(id)
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WebAuthnUserDetails loadUserByUsername(String username) {
		return userEntityRepository.findOneByEmailAddress(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("UserEntity with username'%s' is not found.", username)));
	}

	@Override
	public WebAuthnUserDetails loadUserByCredentialId(byte[] credentialId) {
		AuthenticatorEntity authenticatorEntity = authenticatorEntityRepository.findOneByCredentialId(credentialId)
				.orElseThrow(() -> new CredentialIdNotFoundException(String.format("AuthenticatorEntity with credentialId'%s' is not found.", Base64UrlUtil.encodeToString(credentialId))));
		return authenticatorEntity.getUser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserEntity createUser(UserEntity user) {
		userEntityRepository.findOneByEmailAddress(user.getEmailAddress()).ifPresent((retrievedUserEntity) -> {
			throw new WebAuthnSampleBusinessException(MessageCodes.Error.User.EMAIL_ADDRESS_IS_ALREADY_USED);
		});
		return userEntityRepository.save(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUser(UserEntity user) {

		UserEntity userEntity = userEntityRepository.findById(user.getId())
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
		userEntityRepository.save(userEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUser(String username) {
		UserEntity userEntity = userEntityRepository.findOneByEmailAddress(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("UserEntity with username'%s' is not found.", username)));
		userEntityRepository.delete(userEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUser(int id) {
		userEntityRepository.findById(id)
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
		userEntityRepository.deleteById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changePassword(String oldPassword, String newPassword) {
		UserEntity currentUserEntity = getCurrentUser();

		if (currentUserEntity == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change rawPassword as no Authentication object found in context "
							+ "for current user.");
		}

		currentUserEntity.setPassword(newPassword);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean userExists(String username) {
		return userEntityRepository.findOneByEmailAddress(username).isPresent();
	}

	/**
	 * return current login user
	 *
	 * @return login user
	 */
	private UserEntity getCurrentUser() {
		return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}


	@Override
	public void addAuthenticator(String username, Authenticator authenticator) {
		UserEntity userEntity = userEntityRepository.findOneByEmailAddress(username)
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
		AuthenticatorEntity authenticatorEntity = modelMapper.map(authenticator, AuthenticatorEntity.class);
		authenticatorEntity.setUser(userEntity);
		userEntity.getAuthenticators().add(authenticatorEntity);
	}

	@Override
	public void removeAuthenticator(String username, Authenticator authenticator) {
		UserEntity userEntity = userEntityRepository.findOneByEmailAddress(username)
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
		boolean found = userEntity.getAuthenticators().remove(authenticator);
		if (!found) {
			throw new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.Authenticator.AUTHENTICATOR_NOT_FOUND);
		}
	}

	@Override
	public void removeAuthenticator(String username, byte[] credentialId) {
		UserEntity userEntity = userEntityRepository.findOneByEmailAddress(username)
				.orElseThrow(() -> new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.User.USER_NOT_FOUND));
		boolean found = userEntity.getAuthenticators().removeIf(item -> Arrays.equals(item.getAttestedCredentialData().getCredentialId(), credentialId));
		if (!found) {
			throw new WebAuthnSampleEntityNotFoundException(MessageCodes.Error.Authenticator.AUTHENTICATOR_NOT_FOUND);
		}
	}

}
