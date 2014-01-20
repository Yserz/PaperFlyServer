/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.chat.util.decoder.JsonDecoder;
import de.fhb.paperfly.server.chat.util.encoder.JsonEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class Config implements ServerApplicationConfig {

	private Set<ServerEndpointConfig> endpointConfigs = new HashSet<>();

	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
		List<Class<? extends Decoder>> decoders = new ArrayList<>();
		decoders.add(JsonDecoder.class);
		List<Class<? extends Encoder>> encoders = new ArrayList<>();
		encoders.add(JsonEncoder.class);
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/Global").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/223-INFZ").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/141-IWZ").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/Mensa").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/Audimax").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/Bibliothek").decoders(decoders).encoders(encoders).build());
		endpointConfigs.add(ServerEndpointConfig.Builder.create(PaperFlyRoomEndpoint.class, "/ws/chat/23-WWZ").decoders(decoders).encoders(encoders).build());

		return endpointConfigs;
	}

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		return null;
	}
}
