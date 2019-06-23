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

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientInput;
import com.webauthn4j.data.extension.client.ExtensionClientInput;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientInput;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.MFATokenEvaluator;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.webauthn.WebAuthnProcessingFilter;
import org.springframework.security.webauthn.WebAuthnRegistrationRequestValidator;
import org.springframework.security.webauthn.challenge.ChallengeRepository;
import org.springframework.security.webauthn.endpoint.AssertionOptionsEndpointFilter;
import org.springframework.security.webauthn.endpoint.AttestationOptionsEndpointFilter;
import org.springframework.security.webauthn.options.ExtensionOptionProvider;
import org.springframework.security.webauthn.options.OptionsProvider;
import org.springframework.security.webauthn.options.OptionsProviderImpl;
import org.springframework.security.webauthn.options.StaticExtensionOptionProvider;
import org.springframework.security.webauthn.server.ServerPropertyProvider;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adds WebAuthn authentication. All attributes have reasonable defaults making all
 * parameters are optional. If no {@link #loginPage(String)} is specified, a default login
 * page will be generated by the framework.
 *
 * <h2>Security Filters</h2>
 * <p>
 * The following Filters are populated
 *
 * <ul>
 * <li>{@link WebAuthnProcessingFilter}</li>
 * <li>{@link AttestationOptionsEndpointFilter}</li>
 * <li>{@link AssertionOptionsEndpointFilter}</li>
 * </ul>
 *
 * <h2>Shared Objects Created</h2>
 * <p>
 * The following shared objects are populated
 * <ul>
 * <li>{@link ChallengeRepository}</li>
 * <li>{@link OptionsProvider}</li>
 * <li>{@link ServerPropertyProvider}</li>
 * </ul>
 *
 * <h2>Shared Objects Used</h2>
 * <p>
 * The following shared objects are used:
 *
 * <ul>
 * <li>{@link AuthenticationManager}</li>
 * <li>{@link MFATokenEvaluator}</li>
 * </ul>
 *
 * @see WebAuthnAuthenticationProviderConfigurer
 */
public final class WebAuthnLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
		AbstractAuthenticationFilterConfigurer<H, WebAuthnLoginConfigurer<H>, WebAuthnProcessingFilter> {

	private final AttestationOptionsEndpointConfig attestationOptionsEndpointConfig = new AttestationOptionsEndpointConfig();
	private final AssertionOptionsEndpointConfig assertionOptionsEndpointConfig = new AssertionOptionsEndpointConfig();
	private final PublicKeyCredParamsConfig publicKeyCredParamsConfig = new PublicKeyCredParamsConfig();
	private final AuthenticatorSelectionCriteriaConfig authenticatorSelectionConfig = new AuthenticatorSelectionCriteriaConfig();
	private final ExtensionsClientInputsConfig<RegistrationExtensionClientInput> registrationExtensionsConfig
			= new ExtensionsClientInputsConfig<>();
	private final ExtensionsClientInputsConfig<AuthenticationExtensionClientInput> authenticationExtensionsConfig
			= new ExtensionsClientInputsConfig<>();
	private final ExpectedRegistrationExtensionIdsConfig
			expectedRegistrationExtensionIdsConfig = new ExpectedRegistrationExtensionIdsConfig();
	private final ExpectedAuthenticationExtensionIdsConfig
			expectedAuthenticationExtensionIdsConfig = new ExpectedAuthenticationExtensionIdsConfig();
	//~ Instance fields
	// ================================================================================================
	private OptionsProvider optionsProvider = null;
	private JsonConverter jsonConverter = null;
	private ServerPropertyProvider serverPropertyProvider = null;
	private WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator;
	private String rpId = null;
	private String rpName = null;
	private String rpIcon = null;
	private Long registrationTimeout = null;
	private Long authenticationTimeout = null;
	private AttestationConveyancePreference attestation = null;
	private String usernameParameter = null;
	private String passwordParameter = null;
	private String credentialIdParameter = null;
	private String clientDataJSONParameter = null;
	private String authenticatorDataParameter = null;
	private String signatureParameter = null;
	private String clientExtensionsJSONParameter = null;


	public WebAuthnLoginConfigurer() {
		super(new WebAuthnProcessingFilter(), null);
	}

	public static WebAuthnLoginConfigurer<HttpSecurity> webAuthnLogin() {
		return new WebAuthnLoginConfigurer<>();
	}

	// ~ Methods
	// ========================================================================================================
	@Override
	public void init(H http) throws Exception {
		super.init(http);

		if (jsonConverter == null) {
			jsonConverter = WebAuthnConfigurerUtil.getOrCreateJsonConverter(http);
		}
		http.setSharedObject(JsonConverter.class, jsonConverter);

		if (optionsProvider == null) {
			optionsProvider = WebAuthnConfigurerUtil.getOrCreateOptionsProvider(http);
		}
		if (optionsProvider instanceof OptionsProviderImpl) {
			OptionsProviderImpl optionsProviderImpl = (OptionsProviderImpl) optionsProvider;
			configureOptionsProviderImpl(optionsProviderImpl);
			optionsProvider = optionsProviderImpl;
		}
		http.setSharedObject(OptionsProvider.class, optionsProvider);

		if (serverPropertyProvider == null) {
			serverPropertyProvider = WebAuthnConfigurerUtil.getOrCreateServerPropertyProvider(http);
		}
		http.setSharedObject(ServerPropertyProvider.class, serverPropertyProvider);

		if (webAuthnRegistrationRequestValidator == null) {
			webAuthnRegistrationRequestValidator = WebAuthnConfigurerUtil.getOrCreateWebAuthnRegistrationRequestValidator(http);
		}
		http.setSharedObject(WebAuthnRegistrationRequestValidator.class, webAuthnRegistrationRequestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(H http) throws Exception {
		super.configure(http);
		configureParameters();

		this.getAuthenticationFilter().setServerPropertyProvider(serverPropertyProvider);

		this.attestationOptionsEndpointConfig.configure(http);
		this.assertionOptionsEndpointConfig.configure(http);

		this.getAuthenticationFilter().setExpectedAuthenticationExtensionIds(expectedAuthenticationExtensionIdsConfig.expectedAuthenticationExtensionIds);
		webAuthnRegistrationRequestValidator.setExpectedRegistrationExtensionIds(expectedRegistrationExtensionIdsConfig.expectedRegistrationExtensionIds);
	}

	private void configureOptionsProviderImpl(OptionsProviderImpl optionsProviderImpl) {
		if (rpId != null) {
			optionsProviderImpl.setRpId(rpId);
		}
		if (rpName != null) {
			optionsProviderImpl.setRpName(rpName);
		}
		if (rpIcon != null) {
			optionsProviderImpl.setRpIcon(rpIcon);
		}
		optionsProviderImpl.getPubKeyCredParams().addAll(publicKeyCredParamsConfig.publicKeyCredentialParameters);
		if (registrationTimeout != null) {
			optionsProviderImpl.setRegistrationTimeout(registrationTimeout);
		}
		if (authenticationTimeout != null) {
			optionsProviderImpl.setAuthenticationTimeout(authenticationTimeout);
		}
		AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria(
				this.authenticatorSelectionConfig.authenticatorAttachment,
				this.authenticatorSelectionConfig.requireResidentKey,
				this.authenticatorSelectionConfig.userVerification);
		optionsProviderImpl.setAuthenticatorSelection(authenticatorSelectionCriteria);
		optionsProviderImpl.setAttestation(this.attestation);
		optionsProviderImpl.getRegistrationExtensions().putAll(registrationExtensionsConfig.extensionsClientInputs);
		optionsProviderImpl.getAuthenticationExtensions().putAll(authenticationExtensionsConfig.extensionsClientInputs);

		if (usernameParameter != null) {
			optionsProviderImpl.setUsernameParameter(usernameParameter);
		}
		if (passwordParameter != null) {
			optionsProviderImpl.setPasswordParameter(passwordParameter);
		}
		if (credentialIdParameter != null) {
			optionsProviderImpl.setCredentialIdParameter(credentialIdParameter);
		}
		if (clientDataJSONParameter != null) {
			optionsProviderImpl.setClientDataJSONParameter(clientDataJSONParameter);
		}
		if (authenticatorDataParameter != null) {
			optionsProviderImpl.setAuthenticatorDataParameter(authenticatorDataParameter);
		}
		if (signatureParameter != null) {
			optionsProviderImpl.setSignatureParameter(signatureParameter);
		}
		if (clientExtensionsJSONParameter != null) {
			optionsProviderImpl.setClientExtensionsJSONParameter(clientExtensionsJSONParameter);
		}
	}

	private void configureParameters() {
		if (usernameParameter != null) {
			this.getAuthenticationFilter().setUsernameParameter(usernameParameter);
		}
		if (passwordParameter != null) {
			this.getAuthenticationFilter().setPasswordParameter(passwordParameter);
		}
		if (credentialIdParameter != null) {
			this.getAuthenticationFilter().setCredentialIdParameter(credentialIdParameter);
		}
		if (clientDataJSONParameter != null) {
			this.getAuthenticationFilter().setClientDataJSONParameter(clientDataJSONParameter);
		}
		if (authenticatorDataParameter != null) {
			this.getAuthenticationFilter().setAuthenticatorDataParameter(authenticatorDataParameter);
		}
		if (signatureParameter != null) {
			this.getAuthenticationFilter().setSignatureParameter(signatureParameter);
		}
		if (clientExtensionsJSONParameter != null) {
			this.getAuthenticationFilter().setClientExtensionsJSONParameter(clientExtensionsJSONParameter);
		}
	}

	/**
	 * Specifies the {@link OptionsProvider} to be used.
	 *
	 * @param optionsProvider the {@link OptionsProvider}
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> optionsProvider(OptionsProvider optionsProvider) {
		Assert.notNull(optionsProvider, "optionsProvider must not be null");
		this.optionsProvider = optionsProvider;
		return this;
	}

	/**
	 * Specifies the {@link JsonConverter} to be used.
	 *
	 * @param jsonConverter the {@link JsonConverter}
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> jsonConverter(JsonConverter jsonConverter) {
		Assert.notNull(jsonConverter, "jsonConverter must not be null");
		this.jsonConverter = jsonConverter;
		return this;
	}

	/**
	 * Specifies the {@link ServerPropertyProvider} to be used.
	 *
	 * @param serverPropertyProvider the {@link ServerPropertyProvider}
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> serverPropertyProvider(ServerPropertyProvider serverPropertyProvider) {
		Assert.notNull(serverPropertyProvider, "serverPropertyProvider must not be null");
		this.serverPropertyProvider = serverPropertyProvider;
		return this;
	}


	/**
	 * Returns the {@link AttestationOptionsEndpointConfig} for configuring the {@link AttestationOptionsEndpointFilter}
	 *
	 * @return the {@link AttestationOptionsEndpointConfig}
	 */
	public AttestationOptionsEndpointConfig attestationOptionsEndpoint() {
		return attestationOptionsEndpointConfig;
	}

	/**
	 * Returns the {@link AssertionOptionsEndpointConfig} for configuring the {@link AssertionOptionsEndpointFilter}
	 *
	 * @return the {@link AssertionOptionsEndpointConfig}
	 */
	public AssertionOptionsEndpointConfig assertionOptionsEndpoint() {
		return assertionOptionsEndpointConfig;
	}

	/**
	 * The HTTP parameter to look for the username when performing authentication. Default
	 * is "username".
	 *
	 * @param usernameParameter the HTTP parameter to look for the username when
	 *                          performing authentication
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> usernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "usernameParameter must not be null or empty");
		this.usernameParameter = usernameParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the password when performing authentication. Default
	 * is "password".
	 *
	 * @param passwordParameter the HTTP parameter to look for the password when
	 *                          performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> passwordParameter(String passwordParameter) {
		Assert.hasText(usernameParameter, "passwordParameter must not be null or empty");
		this.passwordParameter = passwordParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the credentialId when performing authentication. Default
	 * is "credentialId".
	 *
	 * @param credentialIdParameter the HTTP parameter to look for the credentialId when
	 *                              performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> credentialIdParameter(String credentialIdParameter) {
		Assert.hasText(usernameParameter, "credentialIdParameter must not be null or empty");
		this.credentialIdParameter = credentialIdParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the clientData when performing authentication. Default
	 * is "clientDataJSON".
	 *
	 * @param clientDataJSONParameter the HTTP parameter to look for the clientDataJSON when
	 *                                performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> clientDataJSONParameter(String clientDataJSONParameter) {
		Assert.hasText(usernameParameter, "clientDataJSONParameter must not be null or empty");
		this.clientDataJSONParameter = clientDataJSONParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the authenticatorData when performing authentication. Default
	 * is "authenticatorData".
	 *
	 * @param authenticatorDataParameter the HTTP parameter to look for the authenticatorData when
	 *                                   performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> authenticatorDataParameter(String authenticatorDataParameter) {
		Assert.hasText(usernameParameter, "authenticatorDataParameter must not be null or empty");
		this.authenticatorDataParameter = authenticatorDataParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the signature when performing authentication. Default
	 * is "signature".
	 *
	 * @param signatureParameter the HTTP parameter to look for the signature when
	 *                           performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> signatureParameter(String signatureParameter) {
		Assert.hasText(usernameParameter, "signatureParameter must not be null or empty");
		this.signatureParameter = signatureParameter;
		return this;
	}

	/**
	 * The HTTP parameter to look for the clientExtensionsJSON when performing authentication. Default
	 * is "clientExtensionsJSON".
	 *
	 * @param clientExtensionsJSONParameter the HTTP parameter to look for the clientExtensionsJSON when
	 *                                      performing authentication
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> clientExtensionsJSONParameter(String clientExtensionsJSONParameter) {
		Assert.hasText(clientExtensionsJSONParameter, "clientExtensionsJSONParameter must not be null or empty");
		this.clientExtensionsJSONParameter = clientExtensionsJSONParameter;
		return this;
	}

	/**
	 * The relying party id for credential scoping
	 *
	 * @param rpId the relying party id
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> rpId(String rpId) {
		Assert.hasText(rpId, "rpId parameter must not be null or empty");
		this.rpId = rpId;
		return this;
	}

	/**
	 * The relying party name
	 *
	 * @param rpName the relying party name
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> rpName(String rpName) {
		Assert.hasText(rpName, "rpName parameter must not be null or empty");
		this.rpName = rpName;
		return this;
	}

	/**
	 * The relying party icon
	 *
	 * @param rpIcon the relying party icon
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> rpIcon(String rpIcon) {
		Assert.hasText(rpIcon, "rpIcon parameter must not be null or empty");
		this.rpIcon = rpIcon;
		return this;
	}

	/**
	 * Returns the {@link PublicKeyCredParamsConfig} for configuring PublicKeyCredParams
	 *
	 * @return the {@link PublicKeyCredParamsConfig}
	 */
	public WebAuthnLoginConfigurer<H>.PublicKeyCredParamsConfig publicKeyCredParams() {
		return this.publicKeyCredParamsConfig;
	}

	/**
	 * The timeout for registration ceremony
	 *
	 * @param registrationTimeout the timeout for registration ceremony
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> registrationTimeout(Long registrationTimeout) {
		this.registrationTimeout = registrationTimeout;
		return this;
	}

	/**
	 * The timeout for authentication ceremony
	 *
	 * @param authenticationTimeout the timeout for authentication ceremony
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> authenticationTimeout(Long authenticationTimeout) {
		this.authenticationTimeout = authenticationTimeout;
		return this;
	}

	/**
	 * Returns the {@link AuthenticatorSelectionCriteriaConfig} for configuring authenticator selection criteria
	 *
	 * @return the {@link AuthenticatorSelectionCriteriaConfig}
	 */
	public AuthenticatorSelectionCriteriaConfig authenticatorSelection() {
		return this.authenticatorSelectionConfig;
	}

	/**
	 * The attestation conveyance preference
	 *
	 * @param attestation the attestation conveyance preference
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> attestation(AttestationConveyancePreference attestation) {
		this.attestation = attestation;
		return this;
	}

	/**
	 * Returns the {@link ExtensionsClientInputsConfig} for configuring registration extensions
	 *
	 * @return the {@link ExtensionsClientInputsConfig}
	 */
	public ExtensionsClientInputsConfig<RegistrationExtensionClientInput> registrationExtensions() {
		return this.registrationExtensionsConfig;
	}

	/**
	 * Returns the {@link ExtensionsClientInputsConfig} for configuring authentication extensions
	 *
	 * @return the {@link ExtensionsClientInputsConfig}
	 */
	public ExtensionsClientInputsConfig<AuthenticationExtensionClientInput> authenticationExtensions() {
		return this.authenticationExtensionsConfig;
	}

	/**
	 * Returns the {@link ExpectedRegistrationExtensionIdsConfig} for configuring the expectedRegistrationExtensionId(s)
	 *
	 * @return the {@link ExpectedRegistrationExtensionIdsConfig}
	 */
	public ExpectedRegistrationExtensionIdsConfig expectedRegistrationExtensionIdsConfig() {
		return this.expectedRegistrationExtensionIdsConfig;
	}

	/**
	 * Returns the {@link ExpectedAuthenticationExtensionIdsConfig} for configuring the expectedAuthenticationExtensionId(s)
	 *
	 * @return the {@link ExpectedAuthenticationExtensionIdsConfig}
	 */
	public ExpectedAuthenticationExtensionIdsConfig expectedAuthenticationExtensionIds() {
		return this.expectedAuthenticationExtensionIdsConfig;
	}


	/**
	 * Forward Authentication Success Handler
	 *
	 * @param forwardUrl the target URL in case of success
	 * @return he {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> successForwardUrl(String forwardUrl) {
		successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
		return this;
	}

	/**
	 * Forward Authentication Failure Handler
	 *
	 * @param forwardUrl the target URL in case of failure
	 * @return he {@link WebAuthnLoginConfigurer} for additional customization
	 */
	public WebAuthnLoginConfigurer<H> failureForwardUrl(String forwardUrl) {
		failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
		return this;
	}

	/**
	 * <p>
	 * Specifies the URL to send users to if login is required. If used with
	 * {@link WebSecurityConfigurerAdapter} a default login page will be generated when
	 * this attribute is not specified.
	 * </p>
	 *
	 * @param loginPage login page
	 * @return the {@link WebAuthnLoginConfigurer} for additional customization
	 */
	@Override
	public WebAuthnLoginConfigurer<H> loginPage(String loginPage) {
		return super.loginPage(loginPage);
	}

	/**
	 * Create the {@link RequestMatcher} given a loginProcessingUrl
	 *
	 * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
	 *                           loginProcessingUrl
	 * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
	 */
	@Override
	protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
		return new AntPathRequestMatcher(loginProcessingUrl, "POST");
	}

	/**
	 * Configuration options for the {@link AttestationOptionsEndpointFilter}
	 */
	public class AttestationOptionsEndpointConfig {

		private String processingUrl = AttestationOptionsEndpointFilter.FILTER_URL;

		private AttestationOptionsEndpointConfig() {
		}

		private void configure(H http) {
			AttestationOptionsEndpointFilter attestationOptionsEndpointFilter;
			ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
			String[] beanNames = applicationContext.getBeanNamesForType(AttestationOptionsEndpointFilter.class);
			if (beanNames.length == 0) {
				attestationOptionsEndpointFilter = new AttestationOptionsEndpointFilter(optionsProvider, jsonConverter);
				attestationOptionsEndpointFilter.setFilterProcessesUrl(processingUrl);
			} else {
				attestationOptionsEndpointFilter = applicationContext.getBean(AttestationOptionsEndpointFilter.class);
			}

			http.addFilterAfter(attestationOptionsEndpointFilter, SessionManagementFilter.class);

		}

		/**
		 * Sets the URL for the options endpoint
		 *
		 * @param processingUrl the URL for the options endpoint
		 * @return the {@link AttestationOptionsEndpointConfig} for additional customization
		 */
		public AttestationOptionsEndpointConfig processingUrl(String processingUrl) {
			this.processingUrl = processingUrl;
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}

	}

	/**
	 * Configuration options for the {@link AssertionOptionsEndpointFilter}
	 */
	public class AssertionOptionsEndpointConfig {

		private String processingUrl = AssertionOptionsEndpointFilter.FILTER_URL;

		private AssertionOptionsEndpointConfig() {
		}

		private void configure(H http) {
			AssertionOptionsEndpointFilter assertionOptionsEndpointFilter;
			ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
			String[] beanNames = applicationContext.getBeanNamesForType(AttestationOptionsEndpointFilter.class);
			if (beanNames.length == 0) {
				assertionOptionsEndpointFilter = new AssertionOptionsEndpointFilter(optionsProvider, jsonConverter);
				assertionOptionsEndpointFilter.setFilterProcessesUrl(processingUrl);
			} else {
				assertionOptionsEndpointFilter = applicationContext.getBean(AssertionOptionsEndpointFilter.class);
			}

			http.addFilterAfter(assertionOptionsEndpointFilter, SessionManagementFilter.class);

		}

		/**
		 * Sets the URL for the options endpoint
		 *
		 * @param processingUrl the URL for the options endpoint
		 * @return the {@link AttestationOptionsEndpointConfig} for additional customization
		 */
		public AssertionOptionsEndpointConfig processingUrl(String processingUrl) {
			this.processingUrl = processingUrl;
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}

	}


	/**
	 * Configuration options for PublicKeyCredParams
	 */
	public class PublicKeyCredParamsConfig {

		private List<PublicKeyCredentialParameters> publicKeyCredentialParameters = new ArrayList<>();

		private PublicKeyCredParamsConfig() {
		}

		/**
		 * Add PublicKeyCredParam
		 *
		 * @param type the {@link PublicKeyCredentialType}
		 * @param alg  the {@link COSEAlgorithmIdentifier}
		 * @return the {@link PublicKeyCredParamsConfig}
		 */
		public PublicKeyCredParamsConfig addPublicKeyCredParams(PublicKeyCredentialType type, COSEAlgorithmIdentifier alg) {
			Assert.notNull(type, "type must not be null");
			Assert.notNull(alg, "alg must not be null");

			publicKeyCredentialParameters.add(new PublicKeyCredentialParameters(type, alg));
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}

	}

	public class AuthenticatorSelectionCriteriaConfig {
		private AuthenticatorAttachment authenticatorAttachment;
		private boolean requireResidentKey = false;
		private UserVerificationRequirement userVerification = UserVerificationRequirement.PREFERRED;

		/**
		 * Sets the authenticator attachment preference
		 *
		 * @param authenticatorAttachment the authenticator attachment
		 * @return the {@link AuthenticatorSelectionCriteriaConfig} for additional customization
		 */
		public WebAuthnLoginConfigurer<H>.AuthenticatorSelectionCriteriaConfig authenticatorAttachment(AuthenticatorAttachment authenticatorAttachment) {
			this.authenticatorAttachment = authenticatorAttachment;
			return this;
		}

		/**
		 * Sets the residentKey requirement preference
		 *
		 * @param requireResidentKey true if requires a resident key
		 * @return the {@link AuthenticatorSelectionCriteriaConfig} for additional customization
		 */
		public WebAuthnLoginConfigurer<H>.AuthenticatorSelectionCriteriaConfig requireResidentKey(boolean requireResidentKey) {
			this.requireResidentKey = requireResidentKey;
			return this;
		}

		/**
		 * Sets the user verification requirement preference
		 *
		 * @param userVerification the user verification preference
		 * @return the {@link AuthenticatorSelectionCriteriaConfig} for additional customization
		 */
		public WebAuthnLoginConfigurer<H>.AuthenticatorSelectionCriteriaConfig userVerification(UserVerificationRequirement userVerification) {
			this.userVerification = userVerification;
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}
	}

	/**
	 * Configuration options for AuthenticationExtensionsClientInputs
	 */
	public class ExtensionsClientInputsConfig<T extends ExtensionClientInput> {

		private Map<String, ExtensionOptionProvider<T>> extensionsClientInputs = new HashMap<>();

		private ExtensionsClientInputsConfig() {
		}

		/**
		 * Put ExtensionOption
		 *
		 * @param extensionOption the T
		 * @return the {@link ExtensionsClientInputsConfig}
		 */
		public ExtensionsClientInputsConfig<T> put(T extensionOption) {
			Assert.notNull(extensionOption, "extensionOption must not be null");
			StaticExtensionOptionProvider<T> extensionOptionProvider = new StaticExtensionOptionProvider<>(extensionOption);
			extensionsClientInputs.put(extensionOptionProvider.getIdentifier(), extensionOptionProvider);
			return this;
		}

		/**
		 * Put ExtensionOptionProvider
		 *
		 * @param extensionOptionProvider the T
		 * @return the {@link ExtensionsClientInputsConfig}
		 */
		public ExtensionsClientInputsConfig<T> put(ExtensionOptionProvider<T> extensionOptionProvider) {
			Assert.notNull(extensionOptionProvider, "extensionOptionProvider must not be null");
			extensionsClientInputs.put(extensionOptionProvider.getIdentifier(), extensionOptionProvider);
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}
	}

	/**
	 * Configuration options for expectedRegistrationExtensionIds
	 */
	public class ExpectedRegistrationExtensionIdsConfig {

		private List<String> expectedRegistrationExtensionIds = null;

		private ExpectedRegistrationExtensionIdsConfig() {
		}

		/**
		 * Add AuthenticationExtensionClientInput
		 *
		 * @param expectedRegistrationExtensionId the expected registration extension id
		 * @return the {@link ExpectedRegistrationExtensionIdsConfig}
		 */
		public ExpectedRegistrationExtensionIdsConfig add(String expectedRegistrationExtensionId) {
			Assert.notNull(expectedRegistrationExtensionId, "expectedRegistrationExtensionId must not be null");
			if (expectedRegistrationExtensionIds == null) {
				expectedRegistrationExtensionIds = new ArrayList<>();
			}
			expectedRegistrationExtensionIds.add(expectedRegistrationExtensionId);
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link ExpectedRegistrationExtensionIdsConfig}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}
	}

	/**
	 * Configuration options for expectedRegistrationExtensionIds
	 */
	public class ExpectedAuthenticationExtensionIdsConfig {

		private List<String> expectedAuthenticationExtensionIds = null;

		private ExpectedAuthenticationExtensionIdsConfig() {
		}

		/**
		 * Add AuthenticationExtensionClientInput
		 *
		 * @param expectedAuthenticationExtensionId the expected authentication extension id
		 * @return the {@link ExpectedAuthenticationExtensionIdsConfig}
		 */
		public ExpectedAuthenticationExtensionIdsConfig add(String expectedAuthenticationExtensionId) {
			Assert.notNull(expectedAuthenticationExtensionId, "expectedAuthenticationExtensionId must not be null");
			if (expectedAuthenticationExtensionIds == null) {
				expectedAuthenticationExtensionIds = new ArrayList<>();
			}
			expectedAuthenticationExtensionIds.add(expectedAuthenticationExtensionId);
			return this;
		}

		/**
		 * Returns the {@link WebAuthnLoginConfigurer} for further configuration.
		 *
		 * @return the {@link WebAuthnLoginConfigurer}
		 */
		public WebAuthnLoginConfigurer<H> and() {
			return WebAuthnLoginConfigurer.this;
		}
	}

}
