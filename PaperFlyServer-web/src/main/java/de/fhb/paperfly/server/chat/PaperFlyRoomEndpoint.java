package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * Address: http://localhost:8080/PaperFlyServer-web/ws/chat/global
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
@Interceptors({WebServiceLoggerInterceptor.class})
public class PaperFlyRoomEndpoint extends Endpoint {

	@EJB
	private RoomServiceLocal roomService;
	@EJB
	private AccountServiceLocal accountService;
	@EJB
	private ChatController controller;
	@EJB
	private LoggingServiceLocal LOG;
	private Room room;

	public PaperFlyRoomEndpoint() {
	}

	@Override
	public void onOpen(final Session session, EndpointConfig conf) {
		session.getContainer().setDefaultMaxSessionIdleTimeout(1800000l);/*30m*/
		System.out.println("request URI of session: " + session.getRequestURI().toString());
		LOG.log(this.getClass().getName(), Level.INFO, "Opening connection...");
		System.out.println("Opening connection...");

		String roomName = getRoomName(session.getRequestURI().toString());

		try {
			if (session.getUserPrincipal() != null) {
				try {
					session.getBasicRemote().sendObject(new Message(null, "Hello " + accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername() + ", you are in the chat-room \"" + roomName + "\""));
					// May implement a fallback with client sended username
				} catch (EncodeException ex) {
					Logger.getLogger(PaperFlyRoomEndpoint.class.getName()).log(Level.SEVERE, null, ex);
				}

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
				throw new RuntimeException("You are not Authorized!");

			}
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Could not open connection to websocket!", ex);
		}

		session.addMessageHandler(new MessageHandler.Whole<Message>() {
			@Override
			public void onMessage(Message msg) {
				try {
					for (Session sess : session.getOpenSessions()) {

						System.out.println("request URI of session: " + sess.getRequestURI().toString());
						if (sess.isOpen()) {
							try {
								sess.getBasicRemote().sendObject(new Message(accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername(), msg.getBody()));
							} catch (EncodeException ex) {
								Logger.getLogger(PaperFlyRoomEndpoint.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
				} catch (IOException ex) {
					LOG.log(this.getClass().getName(), Level.SEVERE, null, ex);
				}
			}
		});

	}

	@Override
	public void onError(Session session, Throwable error) {

		LOG.log(this.getClass().getName(), Level.INFO, "Catching Error...");
		LOG.log(this.getClass().getName(), Level.SEVERE, "ERROR: " + error.getLocalizedMessage());
		LOG.log(this.getClass().getName(), Level.SEVERE, error.getLocalizedMessage());
		try {
			session.getBasicRemote().sendObject(new Message(400, error.getLocalizedMessage()));
			session.close();
		} catch (IOException | EncodeException ex) {
			Logger.getLogger(PaperFlyRoomEndpoint.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void onClose(Session session, CloseReason reason) {
		LOG.log(this.getClass().getName(), Level.INFO, "Closing connection...");
		System.out.println("Closing connection...");
		try {
			LOG.log(this.getClass().getName(), Level.INFO, "REMOVING USER " + session.getUserPrincipal().getName() + " FROM CHAT " + getRoom().getName());
			controller.removeUserFromChat(getRoom().getName(), session.getUserPrincipal().getName());

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

	private String getRoomName(String URI) {
		int indexOfLastSlash = URI.lastIndexOf("/") + 1;
		int indexOfLastSign = URI.length();


		return URI.subSequence(indexOfLastSlash, indexOfLastSign).toString();
	}
}
