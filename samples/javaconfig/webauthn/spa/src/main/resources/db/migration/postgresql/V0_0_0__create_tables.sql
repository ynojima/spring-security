-- User table  --
CREATE TABLE m_user (
  id                SERIAL          NOT NULL,
  user_handle       bytea           NOT NULL,
  first_name        VARCHAR(32)    NOT NULL,
  last_name         VARCHAR(32)    NOT NULL,
  email_address     VARCHAR(64)    NOT NULL  UNIQUE,
  password          VARCHAR(64)    NOT NULL,
  pwauth_allowed    BOOLEAN         NOT NULL,
  locked            BOOLEAN         NOT NULL
);

-- Group table  --
CREATE TABLE m_group (
  id                SERIAL          NOT NULL,
  group_name        VARCHAR(32)    NOT NULL
);

-- Authority table  --
CREATE TABLE m_authority (
  id                SERIAL          NOT NULL,
  authority         VARCHAR(32)    NOT NULL
);

-- Authenticator table  --
CREATE TABLE m_authenticator(
  id                SERIAL         NOT NULL,
  name              VARCHAR(32)    NOT NULL,
  user_id           INTEGER        NOT NULL  REFERENCES m_user(id),
  counter           BIGINT         NOT NULL,
  aaguid  bytea  NOT NULL,
  credential_id bytea NOT NULL,
  credential_public_key TEXT NOT NULL,
  attestation_statement  TEXT NOT NULL,
);

-- Transport table  --
CREATE TABLE m_transport (
  authenticator_id  INTEGER        NOT NULL REFERENCES  m_authenticator(id),
  transport         VARCHAR(32)    NOT NULL
);

-- User-Group relation  --
CREATE TABLE r_user_group (
  user_id           INTEGER       NOT NULL  REFERENCES m_user(id) ON DELETE CASCADE,
  group_id          INTEGER       NOT NULL  REFERENCES m_group(id) ON DELETE CASCADE
);

-- User-Authority relation --
CREATE TABLE r_user_authority (
  user_id           INTEGER       NOT NULL  REFERENCES m_user(id) ON DELETE CASCADE,
  authority_id      INTEGER       NOT NULL  REFERENCES m_authority(id) ON DELETE CASCADE
);

-- Group-Authority relation --
CREATE TABLE r_group_authority (
  group_id          INTEGER       NOT NULL  REFERENCES m_group(id) ON DELETE CASCADE,
  authority_id      INTEGER       NOT NULL  REFERENCES m_authority(id) ON DELETE CASCADE
);

