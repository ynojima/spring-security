package org.springframework.security.webauthn.server;

import javax.servlet.http.HttpServletRequest;

public interface EffectiveRpIdProvider {

	/**
	 * returns effective rpId based on request origin and configured <code>rpId</code>.
	 *
	 * @param request request
	 * @return effective rpId
	 */
	String getEffectiveRpId(HttpServletRequest request);
}
