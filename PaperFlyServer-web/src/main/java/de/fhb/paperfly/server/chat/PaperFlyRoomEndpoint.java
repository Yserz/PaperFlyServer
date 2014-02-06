package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
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

	public PaperFlyRoomEndpoint() {
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
//		session.getContainer().setDefaultMaxSessionIdleTimeout(1800000l);/*30m*/
//		session.getContainer().setDefaultMaxSessionIdleTimeout(180000l);/*3m*/
		session.getContainer().setDefaultMaxSessionIdleTimeout(300000l);/*5m*/
		StringBuilder log = new StringBuilder("");
		log.append("Opening connection...").append("\n");
		String roomName = getRoomName(session.getRequestURI().toString());
		log.append("SessionID: ").append(session.getId()).append("\n");
		try {
			if (session.getUserPrincipal() != null) {
				try {
					Message hello = new Message(null, "Hello " + accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername() + ", you are in the chat-room \"" + roomName + "\"");
					hello.setType(MessageType.SYSTEM);
					session.getBasicRemote().sendObject(hello);
					for (Session sess : session.getOpenSessions()) {
						if (sess.isOpen()) {
							String username = accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername();
							Message msg = new Message();
							msg.setType(MessageType.SYSTEM);
							msg.setCode(200);
							msg.setSendTime(new Date());
							log.append(username).append(" joined the room ").append(roomName).append("\n");
							msg.setBody(username + " joined the room " + roomName);
							sess.getBasicRemote().sendObject(msg);
						}
					}
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
					log.append("ADDING CHAT ").append(roomName).append("\n");
					controller.addChat(roomName);
				}
				log.append("ADDING USER ").append(session.getUserPrincipal().getName()).append(" TO CHAT ").append(roomName).append("\n");
				controller.addUserToChat(roomName, session.getUserPrincipal().getName());
			} else {
				log.append("User is not Authorized!").append("\n");
				LOG.log(this.getClass().getName(), Level.INFO, log.toString());
				log = new StringBuilder("");
				throw new RuntimeException("You are not Authorized!");
			}
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.INFO, log.toString());
			log = new StringBuilder("");
			LOG.log(this.getClass().getName(), Level.SEVERE, "Could not open connection to websocket!", ex);
		}

		session.addMessageHandler(new MessageHandler.Whole<Message>() {
			@Override
			public void onMessage(Message msg) {
				StringBuilder log = new StringBuilder("");
				try {
					for (Session sess : session.getOpenSessions()) {
						if (sess.isOpen()) {
							try {
								String username = accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername();
								msg.setUsername(username);
								log.append("##################################").append("\n");
								log.append("######### Incoming Message: ").append(msg).append("\n");
								sess.getBasicRemote().sendObject(msg);
							} catch (EncodeException ex) {
								LOG.log(this.getClass().getName(), Level.INFO, log.toString());
								log = new StringBuilder("");
								LOG.log(this.getClass().getName(), Level.SEVERE, "ERROR: ", ex);
							}
						}
					}
				} catch (IOException ex) {
					LOG.log(this.getClass().getName(), Level.INFO, log.toString());
					log = new StringBuilder("");
					LOG.log(this.getClass().getName(), Level.SEVERE, null, ex);
				}
				LOG.log(this.getClass().getName(), Level.INFO, log.toString());
			}
		});
		LOG.log(this.getClass().getName(), Level.INFO, log.toString());

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
		StringBuilder log = new StringBuilder("");
		log.append("Catching Error...").append("\n");
		log.append(error.getLocalizedMessage()).append("\n");
		try {
			session.getBasicRemote().sendObject(new Message(400, error.getLocalizedMessage()));
			session.close();
		} catch (IOException | EncodeException ex) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "ERROR: ", ex);
		}
		LOG.log(this.getClass().getName(), Level.INFO, log.toString());
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

		StringBuilder log = new StringBuilder("");
		try {
			log.append("Closing connection...").append("\n");
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen()) {
					try {
						String username = accountService.getAccountByMail(session.getUserPrincipal().getName()).getUsername();
						Message msg = new Message();
						msg.setType(MessageType.SYSTEM);
						msg.setCode(200);
						msg.setSendTime(new Date());
						log.append(username).append(" left the room ").append(room.getName()).append("\n");
						msg.setBody(username + " left the room " + room.getName());
						sess.getBasicRemote().sendObject(msg);
					} catch (EncodeException | IOException ex) {
						LOG.log(this.getClass().getName(), Level.INFO, log.toString());
						log = new StringBuilder("");
						LOG.log(this.getClass().getName(), Level.SEVERE, "ERROR(Principal is null): " + ex);
					}
				}
			}
			log.append("REMOVING USER ").append(session.getUserPrincipal().getName()).append(" FROM CHAT ").append(getRoom().getName()).append("\n");
			controller.removeUserFromChat(
					getRoom().getName(),
					session.getUserPrincipal().getName());

		} catch (NullPointerException npe) {
			LOG.log(this.getClass().getName(), Level.INFO, log.toString());
			log = new StringBuilder("");
			LOG.log(this.getClass().getName(), Level.SEVERE, null, npe);
		}
		try {
			session.close(reason);
		} catch (IOException ex) {
			LOG.log(this.getClass().getName(), Level.INFO, log.toString());
			log = new StringBuilder("");
			LOG.log(this.getClass().getName(), Level.SEVERE, null, ex);
		}
		LOG.log(this.getClass().getName(), Level.INFO, log.toString());

	}

	/**
	 * get room
	 * @return 
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * set room
	 * 
	 * @param room 
	 */
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
