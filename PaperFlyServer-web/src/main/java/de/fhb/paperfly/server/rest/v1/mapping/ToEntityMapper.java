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
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.dto.input.CredentialDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps DTOs to entities.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
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
//		e.setEnabled(dto.isEnabled());

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

	/**
	 * mapp account list
	 *
	 * @param dtoList
	 * @return
	 */
	public List<Account> mapAccountList(List<AccountDTO> dtoList) {

		List<Account> eList = new ArrayList<Account>();
		for (AccountDTO dto : dtoList) {
			eList.add(mapAccount(dto));
		}

		return eList;
	}
}
