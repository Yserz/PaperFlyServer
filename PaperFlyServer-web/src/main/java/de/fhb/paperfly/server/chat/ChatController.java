/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.chat;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.service.RoomServiceLocal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;

/**
 *
 * @author MacYser
 */
@Stateless
public class ChatController {

	@EJB
	private RoomServiceLocal roomService;
	@EJB
	private AccountServiceLocal accountService;
	private Map<String, PaperFlyChat> chats;

	public ChatController() {
		chats = new HashMap<>();
	}

	public void addChat(String room, PaperFlyChat chat) {
		chats.put(room, chat);
	}

	public void removeChat(String room) {
		chats.remove(room);
	}

	public List<Account> getUsersInRoom(Room room) {
		List<Account> accountList = new ArrayList<>();

		for (Session session : chats.get(room.getName()).getSessions()) {
			if (session.getUserPrincipal() != null) {
				accountList.add(accountService.getAccountByUsername(session.getUserPrincipal().getName()));
			}
		}
		return accountList;
	}

	public Room locateAccount(String username) {
		for (Map.Entry<String, PaperFlyChat> chatEntry : chats.entrySet()) {
			for (Session session : chatEntry.getValue().getSessions()) {
				if (session.getUserPrincipal() != null && session.getUserPrincipal().getName().equals(username)) {
					return roomService.getRoomByRoomName(chatEntry.getKey());
				}
			}
		}
		return null;
	}

	public Map<String, PaperFlyChat> getChats() {
		return chats;
	}

	public void setChats(Map<String, PaperFlyChat> chats) {
		this.chats = chats;
	}
}
