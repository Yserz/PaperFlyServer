/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.interceptor.WebServiceLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Singleton
@Startup
@Interceptors({WebServiceLoggerInterceptor.class})
public class ChatController {

	@EJB
	private RoomServiceLocal roomService;
	@EJB
	private AccountServiceLocal accountService;
	@EJB
	private LoggingServiceLocal LOG;
	private Map<String, Set<String>> chats;

	public ChatController() {
		chats = new HashMap<>();
	}

	public void addChat(String room) {
		chats.put(room, new HashSet<String>());
	}

	public void removeChat(String room) {
		chats.remove(room);
	}

	public void addUserToChat(String room, String user) {
		chats.get(room).add(user);
	}

	public void removeUserFromChat(String room, String user) {
		chats.get(room).remove(user);
	}

	public List<Account> getUsersInRoom(Room room) {
		LOG.log(this.getClass().getName(), Level.INFO, "Room: " + room);
		List<Account> accountList = new ArrayList<>();
		LOG.log(this.getClass().getName(), Level.INFO, "Chat: " + chats.get(room.getName()));
		if (chats.get(room.getName()) != null) {
			for (String user : chats.get(room.getName())) {
				accountList.add(accountService.getAccountByMail(user));

			}
		}
		return accountList;
	}

	public Room locateAccount(String mail) {
		for (Map.Entry<String, Set<String>> chatEntry : chats.entrySet()) {
			chatEntry.getValue().contains(mail);
			Room room = roomService.getRoomByRoomName(chatEntry.getKey());

			if (room != null
					&& room.getCoordinate() != null
					&& room.getCoordinate().getLatitude() != 0
					&& room.getCoordinate().getLonglitutde() != 0) {
				return room;
			}
		}
		return null;
	}

	public Map<String, Set<String>> getChats() {
		return chats;
	}

	public void setChats(Map<String, Set<String>> chats) {
		this.chats = chats;
	}
}
