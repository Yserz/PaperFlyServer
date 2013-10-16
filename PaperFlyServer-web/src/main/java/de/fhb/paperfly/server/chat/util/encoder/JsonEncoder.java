package de.fhb.paperfly.server.chat.util.encoder;

import com.google.gson.Gson;
import de.fhb.paperfly.server.chat.Message;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author MacYser
 */
public class JsonEncoder implements Encoder.Text<Message> {

	private static final Logger LOG = Logger.getLogger(JsonEncoder.class.getName());
	private Gson gson;

	@Override
	public String encode(Message msg) throws EncodeException {
		LOG.log(Level.INFO, "JsonEncoder encode");
		return gson.toJson(msg);
	}

	@Override
	public void init(EndpointConfig config) {
		LOG.log(Level.INFO, "Init JsonEncoder");
		gson = new Gson();
	}

	@Override
	public void destroy() {
		LOG.log(Level.INFO, "Destroying JsonEncoder");
		gson = null;
	}
}
