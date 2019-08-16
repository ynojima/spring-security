package org.springframework.security.webauthn;

import com.webauthn4j.util.Base64UrlUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.webauthn.challenge.WebAuthnChallengeRepository;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetails;
import org.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WebAuthnOptionWebHelper {

	private WebAuthnChallengeRepository challengeRepository;
	private WebAuthnUserDetailsService userDetailsService;

	public WebAuthnOptionWebHelper(WebAuthnChallengeRepository challengeRepository, WebAuthnUserDetailsService userDetailsService) {
		this.challengeRepository = challengeRepository;
		this.userDetailsService = userDetailsService;
	}

	public String getChallenge(HttpServletRequest request){
		return Base64UrlUtil.encodeToString(challengeRepository.loadOrGenerateChallenge(request).getValue());
	}

	public List<String> getCredentialIds(String username){
		if(username == null){
			return Collections.emptyList();
		}
		else {
			try{
				WebAuthnUserDetails webAuthnUserDetails = userDetailsService.loadUserByUsername(username);
				return webAuthnUserDetails.getAuthenticators().stream()
						.map(authenticator -> Base64UrlUtil.encodeToString(authenticator.getCredentialId()))
						.collect(Collectors.toList());
			}
			catch (UsernameNotFoundException e){
				return Collections.emptyList();
			}
		}
	}
}
