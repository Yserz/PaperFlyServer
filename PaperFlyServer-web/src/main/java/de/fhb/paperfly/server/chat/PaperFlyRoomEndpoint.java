package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
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
 * This class represents a websocket endpoint for every room. Example address:
 * http://localhost:8080/PaperFlyServer-web/ws/chat/global
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
	private final int latestMessagesLength = 10;
	private LimitedQueue<Message> latestMessages;

	public PaperFlyRoomEndpoint() {
		latestMessages = new LimitedQueue<>(latestMessagesLength);
	}

	/**
	 * This method is invoked if a connection to the websocket endpoint is
	 * etablished. The room is filtered out of the request URI.
	 *
	 * @param session The session of the account whos connecting to the
	 * endpoint.
	 * @param conf The configuration of the websocket endpoint.
	 */
	@Override
	public void onOpen(final Session session, EndpointConfig conf) {
		session.getContainer().setDefaultMaxSessionIdleTimeout(1800000l);/*30m*/
		LOG.log(this.getClass().getName(), Level.INFO, "Opening connection...");
		LOG.log(this.getClass().getName(), Level.INFO, "Request URI of session: " + session.getRequestURI().toString());
		String roomName = getRoomName(session.getRequestURI().toString());

		try {
			if (session.getUserPrincipal() != null) {
				try {
					session.getBasicRemote().sendObject(new Message(null, "Hello " + accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername() + ", you are in the chat-room \"" + roomName + "\""));
					// show latest messages in this chat
					String temp = "";
					for (Object m : latestMessages) {
						if (m != null) {
							session.getBasicRemote().sendObject((Message) m);
							temp += "m: " + ((Message) m).toString() + "\n";
						}
					}
					System.out.println(temp);

				} catch (EncodeException ex) {
					Logger.getLogger(PaperFlyRoomEndpoint.class.getName()).log(Level.SEVERE, null, ex);
				}

				if (!controller.getChats().containsKey(roomName)) {
					Room room = roomService.getRoomByRoomName(roomName);
					if (room == null) {
						throw new RuntimeException("Theres no valid room given!");
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
						if (sess.isOpen()) {
							try {
								String username = accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername();
								sess.getBasicRemote().sendObject(new Message(username, msg.getBody()));
								synchronized (latestMessages) {
									latestMessages.add(new Message(username, msg.getBody()));
									System.out.println("Messages:\n" + latestMessages);
								}
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

	/**
	 * This method is called if any exception is raised by the endpoint. If
	 * possible a message is send to the client with a specific error-message.
	 *
	 * @param session The session of the account whos connecting to the
	 * endpoint.
	 * @param error The error thrown by the endpoint.
	 */
	@Override
	public void onError(Session session, Throwable error) {
		LOG.log(this.getClass().getName(), Level.INFO, "Catching Error...");
		LOG.log(this.getClass().getName(), Level.SEVERE, error.getLocalizedMessage());
		try {
			session.getBasicRemote().sendObject(new Message(400, error.getLocalizedMessage()));
			session.close();
		} catch (IOException | EncodeException ex) {
			Logger.getLogger(PaperFlyRoomEndpoint.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * This method is called if the connection to the endpoint is closed. The
	 * account will be deleted from the user-chat-list and the session will be
	 * closed.
	 *
	 * @param session The session of the account whos connecting to the
	 * endpoint.
	 * @param reason The closereason.
	 */
	@Override
	public void onClose(Session session, CloseReason reason) {

		try {
			LOG.log(this.getClass().getName(), Level.INFO, "Closing connection...");
			LOG.log(this.getClass().getName(), Level.INFO,
					"REMOVING USER " + session.getUserPrincipal().getName()
					+ " FROM CHAT " + getRoom().getName());
			controller.removeUserFromChat(
					getRoom().getName(),
					session.getUserPrincipal().getName());

		} catch (NullPointerException npe) {
			LOG.log(this.getClass().getName(), Level.SEVERE, null, npe);
		}
		try {
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

	/**
	 * Gets the roomname out of a URI-String.
	 *
	 * @param URI The URI-String to parse.
	 * @return The parsed roomname.
	 */
	private String getRoomName(String URI) {
		int indexOfLastSlash = URI.lastIndexOf("/") + 1;
		int indexOfLastSign = URI.length();

		return URI.subSequence(indexOfLastSlash, indexOfLastSign).toString();
	}
}
