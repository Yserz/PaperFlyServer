package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.chat.util.decoder.JsonDecoder;
import de.fhb.paperfly.server.chat.util.encoder.JsonEncoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
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
 * @author MacYser
 */
@ServerEndpoint(
		value = "/ws/chat/{room-name}",
		encoders = {JsonEncoder.class},
		decoders = {JsonDecoder.class})
public class PaperFlyChat {

	private static final Logger LOG = Logger.getLogger(PaperFlyChat.class.getName());

	@OnOpen
	public void open(Session session, EndpointConfig conf, @PathParam("room-name") String roomName) {
		session.getContainer().setDefaultMaxSessionIdleTimeout(60000l);
		System.out.println("Opening connection...");
		System.out.println("Opened sessions: ");
		try {
			if (session.getUserPrincipal() != null) {
				conf.getUserProperties().put("username", session.getUserPrincipal().getName());
				session.getBasicRemote().sendText("Hello " + session.getUserPrincipal().getName() + ", you are in the chat-room \"" + roomName + "\"");
			} else {
				LOG.log(Level.SEVERE, "User is not Authorized!");
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	@OnMessage
	public void onMessage(Session session, Message msg) {
		try {
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen()) {
					sess.getBasicRemote().sendText(msg.getUsername() + ": " + msg.getText());
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
	public void error(Session session, Throwable error) {
		System.out.println("Catching Error...");
		System.out.println("ERROR: " + error.getMessage());
	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		System.out.println("Closing connection...");
		try {
//			session.getBasicRemote().sendText("Ciao");
			session.close(reason);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}
}
