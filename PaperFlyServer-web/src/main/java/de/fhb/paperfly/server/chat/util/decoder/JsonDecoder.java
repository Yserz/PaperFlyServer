package de.fhb.paperfly.server.chat.util.decoder;

import com.google.gson.Gson;
import de.fhb.paperfly.server.chat.Message;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * This is a websocket decoder. It will try to decode all incoming Messages to a
 * Message of type {@link de.fhb.paperfly.server.chat.Message}.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class JsonDecoder implements Decoder.Text<Message> {

	private Gson gson;

	@Override
	public Message decode(String s) throws DecodeException {
		try {
			Message msg = gson.fromJson(s, Message.class);
			return msg;
		} catch (Exception ex) {
			Logger.getLogger(JsonDecoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public boolean willDecode(String s) {
		boolean canDecode = true;
		return canDecode;
	}

	@Override
	public void init(EndpointConfig config) {
		gson = new Gson();
	}

	@Override
	public void destroy() {
		gson = null;
	}
}
