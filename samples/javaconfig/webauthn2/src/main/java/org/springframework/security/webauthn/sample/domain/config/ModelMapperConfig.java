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

package org.springframework.security.webauthn.sample.domain.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.webauthn.sample.domain.entity.UserEntity;
import org.springframework.security.webauthn.sample.util.modelmapper.PageImplConverter;
import org.springframework.security.webauthn.sample.util.modelmapper.PageImplProvider;
import org.springframework.security.webauthn.sample.util.modelmapper.StringToChallengeConverter;

/**
 * ModelMapper Configuration
 */
@Configuration
public class ModelMapperConfig {


	/**
	 * creates ModelMapper instance
	 *
	 * @return ModelMapper
	 */
	public static ModelMapper createModelMapper() {
		return new ModelMapperConfig().modelMapper();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new PageImplConverter<org.springframework.security.webauthn.sample.domain.entity.UserEntity, UserEntity>(modelMapper));
		modelMapper.addConverter(new StringToChallengeConverter());

		modelMapper.createTypeMap(Page.class, PageImpl.class).setProvider(new PageImplProvider());
		modelMapper.getTypeMap(PageImpl.class, PageImpl.class).setProvider(new PageImplProvider());

		modelMapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

		return modelMapper;
	}

}
