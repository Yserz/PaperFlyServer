/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.context;

import com.sun.jersey.oauth.server.spi.OAuthConsumer;
import com.sun.jersey.oauth.server.spi.OAuthToken;
import com.sun.jersey.oauth.signature.OAuthParameters;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author MacYser
 */
public class OAuthSecurityContext implements SecurityContext {

	private final OAuthConsumer consumer;
	private final OAuthToken token;
	private final boolean isSecure;

	public OAuthSecurityContext(OAuthConsumer consumer, boolean isSecure) {
		this.consumer = consumer;
		this.token = null;
		this.isSecure = isSecure;
	}

	public OAuthSecurityContext(OAuthToken token, boolean isSecure) {
		this.consumer = null;
		this.token = token;
		this.isSecure = isSecure;
	}

	@Override
	public Principal getUserPrincipal() {
		System.out.println("OAuthSecurityContext getUserPrincipal");
		return consumer == null ? token.getPrincipal() : consumer.getPrincipal();
	}

	@Override
	public boolean isUserInRole(String string) {
		System.out.println("OAuthSecurityContext isUserInRole");
		return consumer == null ? token.isInRole(string) : consumer.isInRole(string);
	}

	@Override
	public boolean isSecure() {
		System.out.println("OAuthSecurityContext isSecure");
		return isSecure;
	}

	@Override
	public String getAuthenticationScheme() {
		System.out.println("OAuthSecurityContext getAuthenticationScheme");
		return OAuthParameters.SCHEME;
	}
}
