/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.resources.room;

import com.qmino.miredot.annotations.ReturnType;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.ErrorDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import java.util.ArrayList;
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
// Path: room/
@Stateless
public class RoomResource {

	@EJB
	public LoggingServiceLocal LOG;

	@GET
	@Path("locateAccount/{username}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO")
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
	@Path("/")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO>>")
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

	@GET
	@Path("accounts/{roomID}")
	@Produces(PaperFlyRestService.JSON_MEDIA_TYPE)
	@ReturnType("java.util.List<de.fhb.paperfly.server.rest.v1.dto.AccountDTO>>")
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
}
