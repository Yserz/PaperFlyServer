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

	/**
	 * Method to edit an account.
	 *
	 * @param account The account with the new data.
	 * @return The edited account.
	 */
	Account editAccount(Account account);

	/**
	 * Method to search for accounts by username.
	 *
	 * @param query String that must be present in the accounts username.
	 * @return A list of accounts.
	 */
	List<Account> searchAccountByUsername(String query);

	/**
	 * Method to search exactly one account by username.
	 *
	 * @param username The username of the account.
	 * @return The account or null if account doesnt exists.
	 */
	Account getAccountByUsername(String username);

	/**
	 * This method registers a new account.
	 *
	 * @param firstname
	 * @param lastname
	 * @param username The username of the account. Has to be unique.
	 * @param mail The mail of the account. Has to be unique.
	 * @param password
	 * @param passwordRepeat The repetition of the password for the account.
	 * @return The new account
	 * @throws Exception
	 */
	Account register(String firstname, String lastname, String username, String mail, String password, String passwordRepeat) throws Exception;

	/**
	 * Method to search exactly one account by mail.
	 *
	 * @param mail The mail of the account.
	 * @return The account or null if account doesnt exists.
	 */
	Account getAccountByMail(String mail);
}
