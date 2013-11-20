package de.fhb.paperfly.server.account.service;

import de.fhb.paperfly.server.account.entity.Account;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author MacYser
 */
@Local
public interface AccountServiceLocalAdmin {

//	void changePassword(String mail, String oldPassword, String password, String passwordRepeat) throws Exception;
	Account getAccount(String mail);

	Account editAccount(Account account);

	List<Account> searchAccount(String query);

	Account getAccountByUsername(String username);

	Account registerNewAdmin(String firstname, String lastname, String username, String mail, String password, String passwordRepeat) throws Exception;

	Account registerNewUser(String firstname, String lastname, String username, String mail, String password, String passwordRepeat) throws Exception;
}
