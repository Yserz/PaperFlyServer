/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.resources.auth;

import com.qmino.miredot.annotations.ReturnType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.output.TokenDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author MacYser
 */
@Stateless
@Path("auth/")
public class AuthResource {

	@EJB
	public LoggingServiceLocal LOG;

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
	public Response login(@Context HttpServletRequest request, @Context OAuthProvider provider) {
		Response resp;
		try {
			request.login(request.getHeader("user"), request.getHeader("pw"));
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
	public Response logout(@Context HttpServletRequest request) {
		Response resp;
		resp = Response.ok().build();
		return resp;
	}
}
