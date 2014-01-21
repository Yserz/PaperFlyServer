/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.chat;

import java.util.Date;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is representing a message over websockets.
 * {@link de.fhb.paperfly.server.chat.util.decoder.JsonDecoder} and
 * {@link de.fhb.paperfly.server.chat.util.encoder.JsonEncoder} will parse the
 * message to a JSON. The type of a message can be
 * {@link de.fhb.paperfly.server.chat.MessageType}.TEXT or
 * {@link de.fhb.paperfly.server.chat.MessageType}.ERROR if an error-code is
 * given.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
//@NoArgsConstructor
//@AllArgsConstructor
public class Message {

	private String username;
	private int code;
	private MessageType type;
	private Date sendTime;
	@Transient
	private String room;
	private String body;

	public Message() {
	}

	public Message(String username, String body) {
		this.body = body;
		this.code = 200;
		this.username = username;
		this.type = MessageType.TEXT;
	}

	public Message(int code, String body) {
		this.body = body;
		this.code = code;
		this.type = MessageType.ERROR;
	}
}
