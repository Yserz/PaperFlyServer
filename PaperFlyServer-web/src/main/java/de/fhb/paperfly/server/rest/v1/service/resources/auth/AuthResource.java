/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.resources.auth;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import de.fhb.paperfly.server.rest.v1.dto.input.TokenDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
// Path: auth/
@Stateless
public class AuthResource {

	@GET
	@Path("login")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response login(@Context HttpServletRequest request, @Context OAuthProvider provider) {
		System.out.println("Calling login");
		/**
		 * //Server-Side-Login accountService.login(email, password);
		 */
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
			PaperFlyRestService.LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("logout")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response logout(@Context HttpServletRequest request) {

		Response resp;
		resp = Response.ok().build();
		return resp;
	}
}
