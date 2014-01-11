/*
 * Copyright (C) 2013 Michael Koppen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhb.paperfly.server.rest.v1.service.context;

import com.sun.jersey.oauth.server.spi.OAuthConsumer;
import com.sun.jersey.oauth.server.spi.OAuthToken;
import com.sun.jersey.oauth.signature.OAuthParameters;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
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
