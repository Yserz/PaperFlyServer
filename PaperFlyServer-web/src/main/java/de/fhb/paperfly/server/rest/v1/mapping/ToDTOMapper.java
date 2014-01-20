/*
 * Copyright (C) 2013 Michael Koppen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhb.paperfly.server.rest.v1.mapping;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.output.RoomDTO;
import de.fhb.paperfly.server.room.entity.Room;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class ToDTOMapper {

	private boolean checkDept(int dept) {
		return dept <= 0;
	}

	private AccountDTO mapAccountDept(Account e, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		dept--;
		AccountDTO dto = new AccountDTO();

		dto.setEmail(e.getEmail());
		dto.setFirstName(e.getFirstName());
		dto.setLastName(e.getLastName());
		dto.setUsername(e.getUsername());
		dto.setCreated(e.getCreated());
		dto.setLastModified(e.getLastModified());
		dto.setStatus(Status.valueOf(e.getStatus().name()));
		dto.setFriendListUsernames(e.getFriendListUsernames());

//		dto.setEnabled(e.isEnabled());

		return dto;
	}

//	private List<AccountDTO> mapAccountListDept(List<Account> eList, int dept) {
//		if (checkDept(dept)) {
//			return null;
//		}
//		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
//		for (Account e : eList) {
//			dtoList.add(mapAccountDept(e, dept));
//		}
//
//		return dtoList;
//	}
	private RoomDTO mapRoomDept(Room e, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		dept--;

		RoomDTO dto = new RoomDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setCoordinate(e.getCoordinate());
		dto.setBuilding(e.getBuilding());

		return dto;
	}

	// PUBLISHED METHODS
	public AccountDTO mapAccount(Account e) {
		return mapAccountDept(e, 2);
	}

	public AccountDTO mapAccountWithDepth(Account e, int depth) {
		return mapAccountDept(e, depth);
	}

	public List<AccountDTO> mapAccountList(List<Account> eList) {

		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
		for (Account e : eList) {
			dtoList.add(mapAccount(e));
		}

		return dtoList;
	}

	public List<AccountDTO> mapAccountListWithDepth(List<Account> eList, int depth) {

		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
		for (Account e : eList) {
			dtoList.add(mapAccountWithDepth(e, depth));
		}

		return dtoList;
	}

	public RoomDTO mapRoom(Room e) {
		return mapRoomDept(e, 1);
	}

	public List<RoomDTO> mapRoomList(List<Room> eList) {

		List<RoomDTO> dtoList = new ArrayList<RoomDTO>();
		for (Room e : eList) {
			dtoList.add(mapRoom(e));
		}

		return dtoList;
	}
}
