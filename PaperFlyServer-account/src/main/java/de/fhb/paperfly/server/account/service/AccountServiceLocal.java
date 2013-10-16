package de.fhb.paperfly.server.account.service;

import de.fhb.paperfly.server.account.entity.Account;
import javax.ejb.Local;

/**
 *
 * @author MacYser
 */
@Local
public interface AccountServiceLocal {

	void changePassword(String mail, String oldPassword, String password, String passwordRepeat) throws Exception;

	Account getAccount(String mail);

	Account getAccountByUsername(String username);

	Account registerNewAdmin(String firstName, String name, String accountName, String mail, String password, String passwordRepeat) throws Exception;

	Account registerNewUser(String firstName, String name, String accountName, String mail, String password, String passwordRepeat) throws Exception;
}
