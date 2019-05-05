/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {WebAuthnPublicKeyCredentialUserEntity} from "./web-authn-public-key-credential-user-entity";

export interface OptionsResponse {
  relyingParty: PublicKeyCredentialRpEntity;
  user?: WebAuthnPublicKeyCredentialUserEntity;
  challenge: string;
  pubKeyCredParams: PublicKeyCredentialParameters[];
  registrationTimeout?: number,
  authenticationTimeout?: number,
  credentials: {
    type: PublicKeyCredentialType;
    id: string;
  }[];
  parameters: {
    username: string,
    password: string,
    credentialId: string,
    clientDataJSON: string,
    authenticatorData: string,
    signature: string,
    clientExtensionsJSON: string
  };
}
