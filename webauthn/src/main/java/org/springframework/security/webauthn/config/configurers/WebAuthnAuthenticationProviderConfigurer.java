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

package org.springframework.security.webauthn.config.configurers;

import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.webauthn.WebAuthnAuthenticationProvider;
import org.springframework.security.webauthn.authenticator.WebAuthnAuthenticatorService;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import org.springframework.util.Assert;

/**
 * Allows configuring a {@link WebAuthnAuthenticationProvider}
 *
 * @see WebAuthnConfigurer
 * @see WebAuthnLoginConfigurer
 */
public class WebAuthnAuthenticationProviderConfigurer<
		B extends ProviderManagerBuilder<B>,
		U extends WebAuthnUserDetailsService,
		A extends WebAuthnAuthenticatorService,
		V extends WebAuthnAuthenticationContextValidator>
		extends SecurityConfigurerAdapter<AuthenticationManager, B> {

	//~ Instance fields
	// ================================================================================================
	private U userDetailsService;
	private A authenticatorService;
	private V authenticationContextValidator;

	/**
	 * Constructor
	 *
	 * @param userDetailsService             {@link WebAuthnUserDetailsService}
	 * @param authenticatorService           {@link WebAuthnAuthenticatorService}
	 * @param authenticationContextValidator {@link WebAuthnAuthenticationContextValidator}
	 */
	public WebAuthnAuthenticationProviderConfigurer(U userDetailsService, A authenticatorService, V authenticationContextValidator) {

		Assert.notNull(userDetailsService, "userDetailsService must not be null");
		Assert.notNull(authenticatorService, "authenticatorService must not be null");
		Assert.notNull(authenticationContextValidator, "authenticationContextValidator must not be null");

		this.userDetailsService = userDetailsService;
		this.authenticatorService = authenticatorService;
		this.authenticationContextValidator = authenticationContextValidator;
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public void configure(B builder) {
		WebAuthnAuthenticationProvider authenticationProvider =
				new WebAuthnAuthenticationProvider(userDetailsService, authenticatorService, authenticationContextValidator);
		authenticationProvider = postProcess(authenticationProvider);
		builder.authenticationProvider(authenticationProvider);
	}

}
