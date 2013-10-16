package de.fhb.paperfly.server.rest.v1.mapping;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Group;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
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

		List<String> temp = new ArrayList<String>();
		for (Group lang : e.getGroups()) {
			temp.add(lang.name());
		}
		dto.setGroups(temp);
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

	// PUBLISHED METHODS
	public AccountDTO mapAccount(Account e) {
		return mapAccountDept(e, 1);
	}

	public List<AccountDTO> mapAccountList(List<Account> eList) {

		List<AccountDTO> dtoList = new ArrayList<AccountDTO>();
		for (Account e : eList) {
			dtoList.add(mapAccount(e));
		}

		return dtoList;
	}
}
