package de.fhb.paperfly.server.chat.util.encoder;

import com.google.gson.Gson;
import de.fhb.paperfly.server.chat.Message;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * This is a websocket encoder. It will try to encode all outgoing Messages to a
 * JSON-Message.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class JsonEncoder implements Encoder.Text<Message> {

	private Gson gson;

	@Override
	public String encode(Message msg) throws EncodeException {
		return gson.toJson(msg);
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
