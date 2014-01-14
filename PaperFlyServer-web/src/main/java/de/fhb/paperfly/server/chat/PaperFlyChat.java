package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.chat.util.decoder.JsonDecoder;
import de.fhb.paperfly.server.chat.util.encoder.JsonEncoder;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
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
	private Room room;
	private Set<Session> sessions;
	private static final Logger LOG = Logger.getLogger(PaperFlyChat.class.getName());

	public PaperFlyChat() {
		sessions = new HashSet<>();
	}

	@OnOpen
	public void open(Session session, EndpointConfig conf, @PathParam("room-name") String roomName) throws EncodeException {
		System.out.println("PaperFlyChat " + this);

		session.getContainer().setDefaultMaxSessionIdleTimeout(240000l);/*5m*/
		System.out.println("Opening connection...");
		System.out.println("Opened sessions: ");

		try {
			setSessions(session.getOpenSessions());

			if (session.getUserPrincipal() != null) {
				conf.getUserProperties().put("email", session.getUserPrincipal().getName());
				session.getBasicRemote().sendObject(new Message(null, "Hello " + accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername() + ", you are in the chat-room \"" + roomName + "\""));
				// May implement a fallback with client sended username

				if (!controller.getChats().containsKey(roomName)) {
					Room room = roomService.getRoomByRoomName(roomName);
					if (room == null) {
						setRoom(roomService.createRoom(new Room(null, roomName, null, null)));
					} else {
						setRoom(room);
					}
					System.out.println("ADDING CHAT " + roomName);
					controller.addChat(roomName);
				}
				System.out.println("ADDING USER " + session.getUserPrincipal().getName() + " TO CHAT " + roomName);
				controller.addUserToChat(roomName, session.getUserPrincipal().getName());
			} else {
				LOG.log(Level.SEVERE, "User is not Authorized!");
				session.getBasicRemote().sendObject(new Message(null, "You are not Authorized!"));
				session.close();
			}


		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
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
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	@OnMessage
	public void binaryMessage(Session session, ByteBuffer msg) {
		System.out.println("Binary message: " + msg.toString());
	}

	@OnMessage
	public void pongMessage(Session session, PongMessage msg) {
		System.out.println("Pong message: "
				+ msg.getApplicationData().toString());
	}

	@OnError
	public void error(Session session, Throwable error) throws IOException, EncodeException {
		System.out.println("Catching Error...");
		LOG.log(Level.SEVERE, error.getLocalizedMessage());
		session.getBasicRemote().sendObject(new Message(null, error.getLocalizedMessage()));

	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		System.out.println("Closing connection...");
		try {
			System.out.println("REMOVING USER " + session.getUserPrincipal().getName() + " FROM CHAT " + getRoom().getName());
			controller.removeUserFromChat(getRoom().getName(), session.getUserPrincipal().getName());
			if (controller.getChats().get(getRoom().getName()).isEmpty()) {
				System.out.println("REMOVING CHAT " + getRoom().getName());
				controller.getChats().remove(getRoom().getName());
			}
			session.close(reason);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	private void printSessions(Set<Session> sessions) {
		System.out.println(":::PRINTING SESSIONS:::");
		for (Session session : sessions) {

			System.out.println("ID : " + session.getId());
			if (session.getUserPrincipal() != null) {
				System.out.println("Principal NAME : " + session.getUserPrincipal().getName());
			}

			System.out.println(":::USER PROPERTIES:::");
			if (session.getUserProperties() != null) {
				Object prop;
				for (String key : session.getUserProperties().keySet()) {
					prop = session.getUserProperties().get(key);
					System.out.println("object <" + key + "> : " + prop.toString());
				}
			}
		}
	}

	public Set<Session> getSessions() {
		return sessions;
	}

	private void setSessions(Set<Session> sessions) {
		printSessions(sessions);
		this.sessions = sessions;
	}

	public Room getRoom() {
		return room;
	}

	private void setRoom(Room room) {
		this.room = room;
	}
}
