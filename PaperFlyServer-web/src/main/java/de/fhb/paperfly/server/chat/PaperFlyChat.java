package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.chat.util.decoder.JsonDecoder;
import de.fhb.paperfly.server.chat.util.encoder.JsonEncoder;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingService;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * Address: http://localhost:8080/PaperFlyServer-web/ws/chat/global
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
@Startup
@Interceptors({WebServiceLoggerInterceptor.class})
@ServerEndpoint(
		value = "/ws/chat/{room-name}",
		encoders = {JsonEncoder.class},
		decoders = {JsonDecoder.class})
public class PaperFlyChat {

	@EJB
	private RoomServiceLocal roomService;
	@EJB
	private AccountServiceLocal accountService;
	@EJB
	private ChatController controller;
	@EJB
	private LoggingServiceLocal LOG;
	private Room room;

	public PaperFlyChat() {
	}

	@OnOpen
	public void open(Session session, EndpointConfig conf, @PathParam("room-name") String roomName) throws EncodeException, Exception {
		session.getContainer().setDefaultMaxSessionIdleTimeout(1800000l);/*30m*/
		LOG.log(this.getClass().getName(), Level.INFO, "Opening connection...");
		System.out.println("Opening connection...");

		try {
			if (session.getUserPrincipal() != null) {
				session.getBasicRemote().sendObject(new Message(null, "Hello " + accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername() + ", you are in the chat-room \"" + roomName + "\""));
				// May implement a fallback with client sended username

				if (!controller.getChats().containsKey(roomName)) {
					Room room = roomService.getRoomByRoomName(roomName);
					if (room == null) {
						LOG.log(this.getClass().getName(), Level.INFO, "CREATING ROOM " + roomName);
						setRoom(roomService.createRoom(new Room(null, roomName, null, null)));
					} else {
						setRoom(room);
					}
					LOG.log(this.getClass().getName(), Level.INFO, "ADDING CHAT " + roomName);
					controller.addChat(roomName);
				}
				LOG.log(this.getClass().getName(), Level.INFO, "ADDING USER " + session.getUserPrincipal().getName() + " TO CHAT " + roomName);
				controller.addUserToChat(roomName, session.getUserPrincipal().getName());
			} else {
				LOG.log(this.getClass().getName(), Level.SEVERE, "User is not Authorized!");
//				session.getBasicRemote().sendObject(new Message(null, "You are not Authorized!"));
				throw new Exception("You are not Authorized!");

			}
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Could not open connection to websocket!", ex);
		}
	}

	@OnMessage
	public void onMessage(Session session, Message msg) throws EncodeException {
		try {
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen()) {
					sess.getBasicRemote().sendObject(new Message(accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername(), msg.getBody()));
				}
			}
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.SEVERE, null, ex);
		}
	}

	@OnMessage
	public void binaryMessage(Session session, ByteBuffer msg) {
		LOG.log(this.getClass().getName(), Level.INFO, "Binary message: " + msg.toString());
	}

	@OnMessage
	public void pongMessage(Session session, PongMessage msg) {
		LOG.log(this.getClass().getName(), Level.INFO, "Pong message: "
				+ msg.getApplicationData().toString());
	}

	@OnError
	public void error(Session session, Throwable error) throws IOException, EncodeException {
		LOG.log(this.getClass().getName(), Level.INFO, "Catching Error...");
		LOG.log(this.getClass().getName(), Level.SEVERE, "ERROR: " + error.getLocalizedMessage());
		LOG.log(this.getClass().getName(), Level.SEVERE, error.getLocalizedMessage());
		session.getBasicRemote().sendObject(new Message(400, error.getLocalizedMessage()));
		session.close();
	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		LOG.log(this.getClass().getName(), Level.INFO, "Closing connection...");
		System.out.println("Closing connection...");
		try {
			LOG.log(this.getClass().getName(), Level.INFO, "REMOVING USER " + session.getUserPrincipal().getName() + " FROM CHAT " + getRoom().getName());
			controller.removeUserFromChat(getRoom().getName(), session.getUserPrincipal().getName());

//			if (controller.getChats().get(getRoom().getName()).isEmpty() && !getRoom().getName().equalsIgnoreCase("global")) {
//				System.out.println("REMOVING CHAT " + getRoom().getName());
//				controller.getChats().remove(getRoom().getName());
//			}
			session.close(reason);
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.SEVERE, null, ex);
		}
	}

	public Room getRoom() {
		return room;
	}

	private void setRoom(Room room) {
		this.room = room;
	}
}
