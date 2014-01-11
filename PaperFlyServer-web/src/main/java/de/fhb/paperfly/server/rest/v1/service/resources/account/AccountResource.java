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
package de.fhb.paperfly.server.rest.v1.service.resources.account;

import com.qmino.miredot.annotations.ReturnType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.RegisterAccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.TokenDTO;
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
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
//@Path("account/")
public class AccountResource {

	@EJB
	public LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;

	/**
	 *
	 * [TODO LARGE DESC]
	 *
	 * @title Register a new Account
	 *
	 * @summary Registers an account in the service.
	 *
	 * @param account The account which will be registered.
	 * @param request
	 * @param provider
	 * @return The OAuth-Token for the newly registered account.
	 */
	@PUT
	@Path("register")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@Consumes(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.output.TokenDTO")
	public Response register(RegisterAccountDTO account, @Context HttpServletRequest request, @Context OAuthProvider provider) {
		Response resp;
		try {
			AccountDTO acc = PaperFlyRestService.toDTOMapper.mapAccount(accountService.register(account.getFirstName(), account.getLastName(), account.getUsername(), account.getEmail(), account.getPassword(), account.getPasswordRpt()));
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
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Get an Account
	 * @summary Get's an account which is registered with the given username.
	 * @param username The username of the account to search for.
	 * @param request
	 * @param sc
	 * @return The found account with the given username.
	 */
	@GET
	@Path("{username}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
	public Response getAccount(@PathParam("username") String username, @Context HttpServletRequest request, @Context SecurityContext sc) {
		Response resp;
		try {
			AccountDTO acc = PaperFlyRestService.toDTOMapper.mapAccount(accountService.getAccountByUsername(username));
			resp = Response.ok(acc).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Search Accounts by the Username
	 * @summary Searches an account by the given username. This function will
	 * perform kind of 'LIKE'-Operation.
	 * @param query The query to search for.
	 * @param request
	 * @return The list of accounts with a username which contains the
	 * query-string.
	 */
	@GET
	@Path("search/{query}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.AccountDTO>>")
	public Response searchAccountByUsername(@PathParam("query") String query, @Context HttpServletRequest request) {
		Response resp;
		try {
			List<AccountDTO> accList = PaperFlyRestService.toDTOMapper.mapAccountList(accountService.searchAccountByUsername(query));
			resp = Response.ok(accList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}
}
