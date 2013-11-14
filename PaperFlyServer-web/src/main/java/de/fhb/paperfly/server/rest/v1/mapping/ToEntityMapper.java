package de.fhb.paperfly.server.rest.v1.mapping;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.CredentialDTO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MacYser
 */
public class ToEntityMapper {

	private boolean checkDept(int dept) {
		return dept <= 0;
	}

	private Account mapAccountDept(AccountDTO dto, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		dept--;
		Account e = new Account();
		e.setEmail(dto.getEmail());
		e.setFirstName(dto.getFirstName());
		e.setLastName(dto.getLastName());
		e.setUsername(dto.getUsername());
		e.setCreated(dto.getCreated());
		e.setLastModified(dto.getLastModified());
		e.setEnabled(dto.isEnabled());

		return e;
	}

	private List<Account> mapAccountListDept(List<AccountDTO> dtoList, int dept) {
		if (checkDept(dept)) {
			return null;
		}
		List<Account> eList = new ArrayList<Account>();
		for (AccountDTO dto : dtoList) {
			eList.add(mapAccountDept(dto, dept));
		}

		return eList;
	}

	// PUBLISHED METHODS
	public Account mapAccount(AccountDTO dto) {
		return mapAccountDept(dto, 1);
	}

	public List<Account> mapAccountList(List<AccountDTO> dtoList) {

		List<Account> eList = new ArrayList<Account>();
		for (AccountDTO dto : dtoList) {
			eList.add(mapAccount(dto));
		}

		return eList;
	}
}
