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
package de.fhb.paperfly.server.rest.v1.service.resources.auth;

import com.qmino.miredot.annotations.ReturnType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.TokenDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
//@Path("auth/")
@Interceptors({WebServiceLoggerInterceptor.class})
public class AuthResource {

	@EJB
	public LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Login
	 * @summary Log into the service an retrieve your OAuth-Token.
	 * @param request
	 * @param provider
	 * @return Returns the OAuth-Token of the logged in user.
	 */
	@GET
	@Path("login")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.output.TokenDTO")
	public Response login(@Context HttpServletRequest request, @Context OAuthProvider provider, @Context SecurityContext sc) {
		System.out.println("LOGIN...");
		Response resp;
		try {
			request.login(request.getHeader("user"), request.getHeader("pw"));

			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute("mail", request.getUserPrincipal().toString());
			}

			MultivaluedMap<String, String> roles = new MultivaluedMapImpl();

			if (request.isUserInRole("ADMINISTRATOR")) {
				System.out.println("User is in role ADMINISTRATOR");
				roles.add("roles", "ADMINISTRATOR");
			}
			if (request.isUserInRole("USER")) {
				System.out.println("User is in role USER");
				roles.add("roles", "USER");
			}
			if (request.isUserInRole("ANONYMOUS")) {
				System.out.println("User is in role ANONYMOUS");
				roles.add("roles", "ANONYMOUS");
			}
			DefaultOAuthProvider.Consumer c = ((DefaultOAuthProvider) provider).registerConsumer(request.getUserPrincipal().toString(), request.getUserPrincipal(), roles);
			System.out.println("Consumer Owner: " + c.getOwner());
			System.out.println("Consumer Secret: " + c.getSecret());
			System.out.println("Consumer Key: " + c.getKey());
			System.out.println("Consumer Principal: " + c.getPrincipal());


			String consumerKey = "";
			String callbackURL = "";
			Map<String, List<String>> attributes = null;
//			OAuthToken oauthtoken = provider.newRequestToken(consumerKey, callbackURL, attributes);

			System.out.println("Successfully logged in!");
			System.out.println("User: " + request.getUserPrincipal());

			Account myAccount = accountService.getAccountByMail(sc.getUserPrincipal().getName());
			myAccount.setStatus(Status.ONLINE);
			accountService.editAccount(myAccount);

			resp = Response.ok(new TokenDTO(c.getKey(), c.getSecret())).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Logout
	 * @summary Log's out the actual user.
	 * @param request
	 * @return Nothing to return.
	 */
	@GET
	@Path("logout")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response logout(@Context HttpServletRequest request, @Context SecurityContext sc) throws ServletException {
		System.out.println("LOGOUT...");
		Response resp;

		Account myAccount = accountService.getAccountByMail(sc.getUserPrincipal().getName());
		myAccount.setStatus(Status.OFFLINE);
		accountService.editAccount(myAccount);

		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
		request.logout();

		resp = Response.ok().build();
		return resp;
	}
}
