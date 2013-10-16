package de.fhb.paperfly.server.rest.v1.service;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.ServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.mapping.ToDTOMapper;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * Address: http://localhost:8080/PaperFlyServer-web/rest/service/v1
 *
 * @author MacYser
 */
@Path("/service/v1")
@Stateless
@Interceptors({ServiceLoggerInterceptor.class})
public class RestServiceV1 {

	@EJB
	private LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;
	final String jsonMediaType = "application/json;charset=utf-8";
	private ToDTOMapper toDTOMapper;

	public RestServiceV1() {
		toDTOMapper = new ToDTOMapper();
	}

	@PostConstruct
	private void init() {
	}

	@GET
	@Path("accounts")
	@Produces(jsonMediaType)
	public Response getCategories(@Context Request req) {

		Response resp;
		try {
			//TODO
			List<AccountDTO> accountList = null/*toDTOMapper.mapAccountList(accountService.getAccount(""))*/;
			resp = Response.ok(accountList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	@GET
	@Path("account/{username}")
	@Produces(jsonMediaType)
	public Response getAccount(@PathParam("username") String username, @Context Request req) {

		Response resp;
		try {
			AccountDTO category = toDTOMapper.mapAccount(accountService.getAccountByUsername(username));
			resp = Response.ok(category).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}
}
