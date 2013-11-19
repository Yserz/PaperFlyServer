package de.fhb.paperfly.server.rest.v1.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import com.sun.jersey.oauth.server.spi.OAuthToken;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.ServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.CredentialDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.RegisterAccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.TokenDTO;
import de.fhb.paperfly.server.rest.v1.mapping.ToDTOMapper;
import de.fhb.paperfly.server.rest.v1.mapping.ToEntityMapper;
import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.AbstractMultivaluedMap;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Address: http://localhost:8080/PaperFlyServer-web/rest/service/v1
 *
 * @author MacYser
 */
@Path("/service/v1")
@Stateless
//@Interceptors({ServiceLoggerInterceptor.class})
public class RestServiceV1 {

	@EJB
	private LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;
	final String jsonMediaType = "application/json;charset=utf-8";
	private ToDTOMapper toDTOMapper;
	private ToEntityMapper toEntityMapper;

	public RestServiceV1() {
		toDTOMapper = new ToDTOMapper();
		toEntityMapper = new ToEntityMapper();
	}

	@PostConstruct
	private void init() {
	}

	@GET
	@Path("ping")
	public String ping() {
		return "alive";
	}

	@GET
	@Path("login")
	@Produces(jsonMediaType)
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
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("logout")
	@Produces(jsonMediaType)
	public Response logout(@Context HttpServletRequest request) {

		Response resp;
		resp = Response.ok().build();
		return resp;
	}

	@PUT
	@Path("account")
	@Produces(jsonMediaType)
	@Consumes(jsonMediaType)
	public Response register(RegisterAccountDTO account, @Context HttpServletRequest request, @Context OAuthProvider provider) {

		Response resp;
		try {
			AccountDTO acc = toDTOMapper.mapAccount(accountService.registerNewUser(account.getFirstName(), account.getLastName(), account.getUsername(), account.getEmail(), account.getPassword(), account.getPasswordRpt()));
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
	@Path("account/{username}")
	@Produces(jsonMediaType)
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
			AccountDTO acc = toDTOMapper.mapAccount(accountService.getAccountByUsername(username));
			resp = Response.ok(acc).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@POST
	@Path("account")
	@Produces(jsonMediaType)
	@Consumes(jsonMediaType)
	public Response editAccount(AccountDTO acc, @Context HttpServletRequest request) {

		Response resp;
		try {
			AccountDTO editedAccount = new AccountDTO();/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("account/search/{query}")
	@Produces(jsonMediaType)
	public Response searchAccountByUsername(@PathParam("query") String query, @Context HttpServletRequest request) {

		Response resp;
		try {
			List<AccountDTO> accList = new ArrayList<AccountDTO>();


			accList.add(new AccountDTO());
			accList.add(new AccountDTO());
			/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/


			resp = Response.ok(accList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@POST
	@Path("account/friend/{friendID}")
	@Produces(jsonMediaType)
	@Consumes(jsonMediaType)
	public Response addFriend(AccountDTO acc, @PathParam("friendID") String friendID, @Context HttpServletRequest request) {

		Response resp;
		try {
			AccountDTO editedAccount = new AccountDTO();/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@DELETE
	@Path("account/friend/{friendID}")
	@Produces(jsonMediaType)
	@Consumes(jsonMediaType)
	public Response removeFriend(AccountDTO acc, @PathParam("friendID") String friendID, @Context HttpServletRequest request) {

		Response resp;
		try {
			AccountDTO editedAccount = new AccountDTO();/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("room/accounts/{roomID}")
	@Produces(jsonMediaType)
	public Response getAccountsInRoom(@PathParam("roomID") String roomID, @Context HttpServletRequest request) {

		Response resp;
		try {
			List<AccountDTO> accList = new ArrayList<AccountDTO>();


			accList.add(new AccountDTO());
			accList.add(new AccountDTO());
			/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/


			resp = Response.ok(accList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("room/locateAccount/{username}")
	@Produces(jsonMediaType)
	public Response locateAccount(@PathParam("username") String query, @Context HttpServletRequest request) {

		Response resp;
		try {
			RoomDTO room = new RoomDTO();/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/
			resp = Response.ok(room).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}

	@GET
	@Path("room")
	@Produces(jsonMediaType)
	public Response getRoomList(@Context HttpServletRequest request) {

		Response resp;
		try {
			List<RoomDTO> roomList = new ArrayList<RoomDTO>();


			roomList.add(new RoomDTO());
			roomList.add(new RoomDTO());
			/*toDTOMapper.mapAccount(accountService.getAccountByUsername(username));*/


			resp = Response.ok(roomList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).entity(new ErrorDTO(20, "Fehler")).build();
		}
		return resp;
	}
}
