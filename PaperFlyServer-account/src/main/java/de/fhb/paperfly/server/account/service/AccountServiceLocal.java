package de.fhb.paperfly.server.account.service;

import de.fhb.paperfly.server.account.entity.Account;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author MacYser
 */
@Local
public interface AccountServiceLocal {

	Account editAccount(Account account);

	List<Account> searchAccountByUsername(String query);

	Account getAccountByUsername(String username);

	Account register(String firstname, String lastname, String username, String mail, String password, String passwordRepeat) throws Exception;

	Account getAccountByMail(String mail);
}
