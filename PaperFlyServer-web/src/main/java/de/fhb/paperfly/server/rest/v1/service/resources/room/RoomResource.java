/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.resources.room;

import com.qmino.miredot.annotations.ReturnType;
import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.chat.ChatController;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author MacYser
 */
@Stateless
//@Path("room/")
public class RoomResource {

	@EJB
	private ChatController chatController;
	@EJB
	private RoomServiceLocal roomService;
	@EJB
	private AccountServiceLocal accountService;
	@EJB
	public LoggingServiceLocal LOG;

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Locate an Account
	 * @summary Locates an Account if it's in any.
	 * @param username The accounts username which should be locate.
	 * @param request
	 * @return Returns the room the account is in.
	 */
	@GET
	@Path("locateAccount/{username}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO")
	public Response locateAccount(@PathParam("username") String username, @Context HttpServletRequest request) {
		Response resp;
		try {
			String mail = accountService.getAccountByUsername(username).getEmail();
			Room room = chatController.locateAccount(mail);
			RoomDTO roomDTO = null;
			if (room != null) {
				roomDTO = PaperFlyRestService.toDTOMapper.mapRoom(room);
			}

			resp = Response.ok(roomDTO).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Get all Accounts in a Room
	 * @summary Gets all account located in the given room.
	 * @param roomID The ID of the room to check.
	 * @param request
	 * @return Returns a list of account located in the given room.
	 */
	@GET
	@Path("accounts/{roomID}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.AccountDTO>>")
	public Response getAccountsInRoom(@PathParam("roomID") Long roomID, @Context HttpServletRequest request) {
		Response resp;
		try {
			Room room = roomService.getRoom(roomID);
			List<Account> accList = chatController.getUsersInRoom(room);
			List<AccountDTO> accDTOList = null;
			if (!accList.isEmpty()) {
				accDTOList = PaperFlyRestService.toDTOMapper.mapAccountListWithDepth(accList, 1);
			}
			resp = Response.ok(accDTOList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}

	/**
	 * [TODO LARGE DESC]
	 *
	 * @title Get all Rooms
	 * @summary Gets all rooms.
	 * @param request
	 * @return Returns a list of all rooms.
	 */
	@GET
	@Path("all")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.RoomDTO>>")
	public Response getRoomList(@Context HttpServletRequest request) {
		Response resp;
		try {
			List<RoomDTO> roomList = PaperFlyRestService.toDTOMapper.mapRoomList(roomService.getRoomList());

			resp = Response.ok(roomList).build();
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Exception: {0}", e.getMessage());
			resp = Response.status(500).build();
		}
		return resp;
	}
}
