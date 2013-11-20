/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.resources.account;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.RegisterAccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.TokenDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author MacYser
 */
// Path: account/
@Stateless
public class AccountResource {

	@EJB
	public LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;

	@PUT
	@Path("register")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@Consumes(PaperFlyRestService.JSON_MEDIA_TYPE)
//	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.input.TokenDTO>>")
	public Response register(RegisterAccountDTO account, @Context HttpServletRequest request, @Context OAuthProvider provider) {

		Response resp;
		try {
			AccountDTO acc = PaperFlyRestService.toDTOMapper.mapAccount(accountService.registerNewUser(account.getFirstName(), account.getLastName(), account.getUsername(), account.getEmail(), account.getPassword(), account.getPasswordRpt()));
			request.login(account.getEmail(), account.getPassword());
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

			resp = Response.ok(new TokenDTO(c.getKey(), c.getSecret())).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("{username}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response getAccount(@PathParam("username") String username, @Context HttpServletRequest request, @Context SecurityContext sc) {

//		System.out.println("Username: " + sc.getUserPrincipal().getName());
//		System.out.println("AuthenticationScheme: " + sc.getAuthenticationScheme());
//		System.out.println("isSecure: " + sc.isSecure());
//		System.out.println("isUserInRole(ADMIN): " + sc.isUserInRole("ADMINISTRATOR"));
//		System.out.println("isUserInRole(USER): " + sc.isUserInRole("USER"));
//		System.out.println("isUserInRole(ANONYMOUS): " + sc.isUserInRole("ANONYMOUS"));
		Response resp;
		try {
//			request.login(request.getHeader("user"), request.getHeader("pw"));
//			System.out.println("Successfully logged in!");
//			System.out.println("User: " + request.getUserPrincipal());
			AccountDTO acc = PaperFlyRestService.toDTOMapper.mapAccount(accountService.getAccountByUsername(username));
			resp = Response.ok(acc).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@POST
	@Path("edit/{accountID}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@Consumes(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response editAccount(AccountDTO acc, @Context HttpServletRequest request) {

		Response resp;
		try {
			Account myAccount = accountService.getAccount(acc.getEmail());

			myAccount.setFirstName(acc.getFirstName());
			myAccount.setLastName(acc.getLastName());


			AccountDTO editedAccount = PaperFlyRestService.toDTOMapper.mapAccount(accountService.editAccount(myAccount));
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("search/{query}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response searchAccountByUsername(@PathParam("query") String query, @Context HttpServletRequest request) {

		Response resp;
		try {

			List<AccountDTO> accList = PaperFlyRestService.toDTOMapper.mapAccountList(accountService.searchAccount(query));


			resp = Response.ok(accList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@POST
	@Path("friend/{friendsUsername}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response addFriend(@PathParam("friendsUsername") String friendsUsername, @Context HttpServletRequest request, @Context SecurityContext sc) {

		Response resp;
		try {
			Account myAccount = accountService.getAccountByUsername(sc.getUserPrincipal().getName());
			Account friendsAccount = accountService.getAccountByUsername(friendsUsername);

			myAccount.getFriendList().add(friendsAccount);

			AccountDTO editedAccount = PaperFlyRestService.toDTOMapper.mapAccount(accountService.editAccount(myAccount));

			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@DELETE
	@Path("friend/{friendsUsername}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	public Response removeFriend(@PathParam("friendsUsername") String friendsUsername, @Context HttpServletRequest request, @Context SecurityContext sc) {

		Response resp;
		try {
			Account myAccount = accountService.getAccountByUsername(sc.getUserPrincipal().getName());
			Account friendsAccount = accountService.getAccountByUsername(friendsUsername);

			myAccount.getFriendList().remove(friendsAccount);

			AccountDTO editedAccount = PaperFlyRestService.toDTOMapper.mapAccount(accountService.editAccount(myAccount));

			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}
}
