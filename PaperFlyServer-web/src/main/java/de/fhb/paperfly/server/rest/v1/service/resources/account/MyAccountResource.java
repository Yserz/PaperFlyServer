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
import de.fhb.paperfly.server.account.entity.Status;
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
//@Path("myaccount/")
public class MyAccountResource {

	@EJB
	public LoggingServiceLocal LOG;
	@EJB
	private AccountServiceLocal accountService;

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Edit my Account
	 * @summary Modifies the account of the actual logged in user.
	 * @param account The account with the new data. </br>(ATM only firstname
	 * and lastname will be edited)
	 * @param request
	 * @return The edited account.
	 */
	@POST
	@Path("edit")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@Consumes(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
	public Response editAccount(AccountDTO account, @Context HttpServletRequest request, @Context SecurityContext sc) {
		Response resp;
		try {
			Account myAccount = accountService.getAccountByUsername(sc.getUserPrincipal().getName());
			myAccount.setFirstName(account.getFirstName());
			myAccount.setLastName(account.getLastName());

			AccountDTO editedAccount = PaperFlyRestService.toDTOMapper.mapAccount(accountService.editAccount(myAccount));
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Add a Friend
	 * @summary This operation will add another account to the friendlist of the
	 * actual account.
	 * @param friendsUsername The exact username of the account to add.
	 * @param request
	 * @param sc
	 * @return The account with the modified friendlist.
	 */
	@POST
	@Path("friend/{friendsUsername}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
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
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Remove a Friend
	 * @summary This operation will remove another account from the friendlist
	 * of the actual account.
	 * @param friendsUsername
	 * @param request
	 * @param sc
	 * @return The account with the modified friendlist.
	 */
	@DELETE
	@Path("friend/{friendsUsername}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
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
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Change Online-Status
	 * @summary This operation will change the Online-Status.
	 * @param newStatus The new status of the account.
	 * @param request
	 * @param sc
	 * @return The account with the modified status.
	 */
	@POST
	@Path("status/{status}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
	public Response changeStatus(@PathParam("status") String newStatus, @Context HttpServletRequest request, @Context SecurityContext sc) {
		Response resp;
		try {
			Account myAccount = accountService.getAccountByMail(sc.getUserPrincipal().getName());
			myAccount.setStatus(Status.valueOf(newStatus));
			AccountDTO editedAccount = PaperFlyRestService.toDTOMapper.mapAccount(accountService.editAccount(myAccount));
			resp = Response.ok(editedAccount).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Change Online-Status
	 * @summary This operation will change the Online-Status.
	 * @param newStatus The new status of the account.
	 * @param request
	 * @param sc
	 * @return The account with the modified status.
	 */
	@GET
	@Path("get")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.AccountDTO")
	public Response getAccount(@Context HttpServletRequest request, @Context SecurityContext sc) {
		Response resp;
		try {
			Account myAccount = accountService.getAccountByMail(sc.getUserPrincipal().getName());
			AccountDTO account = PaperFlyRestService.toDTOMapper.mapAccount(myAccount);
			resp = Response.ok(account).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}
}
