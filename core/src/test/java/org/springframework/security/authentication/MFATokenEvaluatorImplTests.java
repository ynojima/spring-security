/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.security.authentication;

import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class MFATokenEvaluatorImplTests {

	// ~ Methods
	// ========================================================================================================
	@Test
	public void testCorrectOperationIsAnonymous() {
		MFATokenEvaluatorImpl mfaTokenEvaluator = new MFATokenEvaluatorImpl();
		assertThat(mfaTokenEvaluator.isMultiFactorAuthentication(new MultiFactorAuthenticationToken("ignored",
				"ignored", AuthorityUtils.createAuthorityList("ignored")))).isTrue();
		assertThat(mfaTokenEvaluator.isMultiFactorAuthentication(new TestingAuthenticationToken("ignored",
				"ignored", AuthorityUtils.createAuthorityList("ignored")))).isFalse();
	}

	@Test
	public void testGettersSetters() {
		MFATokenEvaluatorImpl mfaTokenEvaluator = new MFATokenEvaluatorImpl();

		assertThat(MultiFactorAuthenticationToken.class).isEqualTo(
				mfaTokenEvaluator.getMultiFactorClass());
		mfaTokenEvaluator.setMultiFactorClass(TestingAuthenticationToken.class);
		assertThat(mfaTokenEvaluator.getMultiFactorClass()).isEqualTo(
				TestingAuthenticationToken.class);

	}

}
