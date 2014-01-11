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
 * @author MacYser
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
		dto.setFriendList(mapAccountListDept(e.getFriendList(), dept));

//		dto.setEnabled(e.isEnabled());

		return dto;
	}

	private List<AccountDTO> mapAccountListDept(List<Account> eList, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
		for (Account e : eList) {
			dtoList.add(mapAccountDept(e, dept));
		}

		return dtoList;
	}

	private RoomDTO mapRoomDept(Room e, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		dept--;

		RoomDTO dto = new RoomDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		//TODO
//		dto.setBuilding(null);
//		dto.setCoordinate(null);

		return dto;
	}

	// PUBLISHED METHODS
	public AccountDTO mapAccount(Account e) {
		return mapAccountDept(e, 2);
	}

	public List<AccountDTO> mapAccountList(List<Account> eList) {

		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
		for (Account e : eList) {
			dtoList.add(mapAccount(e));
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
