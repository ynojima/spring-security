package org.springframework.security.authentication;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

public class FirstOfMultiFactorAuthenticationToken extends AbstractAuthenticationToken implements
	Serializable {

	private Object principal;
	private Object credentials;

	// ~ Constructors
	// ===================================================================================================
	public FirstOfMultiFactorAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

}
