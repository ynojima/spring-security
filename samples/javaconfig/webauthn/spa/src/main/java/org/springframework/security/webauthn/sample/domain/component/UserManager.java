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


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.webauthn.sample.domain.entity.UserEntity;

/**
 * ユーザー詳細サービス
 */
public interface UserManager extends UserDetailsService {

	/**
	 * create a userEntity
	 *
	 * @param userEntity userEntity
	 * @return created userEntity
	 */
	UserEntity createUser(UserEntity userEntity);

	/**
	 * update a userEntity
	 *
	 * @param userEntity userEntity
	 */
	void updateUser(UserEntity userEntity);

	/**
	 * delete the specified user
	 *
	 * @param username username
	 */
	void deleteUser(String username);

	/**
	 * delete the specified user
	 *
	 * @param id userId
	 */
	void deleteUser(int id);

	/**
	 * update password
	 *
	 * @param oldPassword old password
	 * @param newPassword new password
	 */
	void changePassword(String oldPassword, String newPassword);

	/**
	 * return true if user exists
	 *
	 * @param username user name
	 * @return true if user exists
	 */
	boolean userExists(String username);

	/**
	 * find a user by id
	 *
	 * @param id userId
	 * @return user
	 */
	UserEntity findById(int id);

}
