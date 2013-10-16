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
package de.fhb.paperfly.server.account.service;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Group;
import de.fhb.paperfly.server.account.repository.AccountRepository;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.util.HashHelper;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * This class provides all business operations on accounts.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AccountService implements AccountServiceLocal, AccountServiceLocalAdmin {

	@EJB
	private LoggingServiceLocal LOG;
	@EJB
	private AccountRepository accountRepository;

	public AccountService() {
	}

	@PostConstruct
	private void init() {
	}

	@Override
	public Account registerNewUser(String firstName, String name, String accountName, String mail, String password, String passwordRepeat)
			throws Exception {
		Account account;
		Account checkAccount;
		String hash = "";

		if (password.equals("") || passwordRepeat.equals("")) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Incomplete password fields!");
			throw new Exception("Incomplete password fields!");
		}

		if (!password.equals(passwordRepeat)) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password not similar to the repetition!");
			throw new Exception("Password not similar to the repetition!");
		}

		if (password.length() < 5) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password too short!");
			throw new Exception("Password too short!");
		}

		account = new Account();
		account.setFirstName(firstName);
		account.setLastName(name);
		account.setUsername(accountName);
		account.setEmail(mail);

		validateAccount(account);

		checkAccount = accountRepository.findAccountByUsername(accountName);

		if (checkAccount != null) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "UserName does already exists!");
			throw new Exception("UserName does already exists!");
		}

		hash = HashHelper.calcSHA256(password);
		account.setPassword(hash);


		ArrayList<Group> groups = new ArrayList<Group>();
		groups.add(Group.USER);
		account.setGroups(groups);


		accountRepository.create(account);
		return account;
	}

	@RolesAllowed({"ADMINISTRATOR"})
	@Override
	public Account registerNewAdmin(String firstName, String name, String accountName, String mail, String password, String passwordRepeat)
			throws Exception {
		Account account;
		Account checkAccount;
		String hash = "";

		if (password.equals("") || passwordRepeat.equals("")) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Incomplete password fields!");
			throw new Exception("Incomplete password fields!");
		}

		if (!password.equals(passwordRepeat)) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password not similar to the repetition!");
			throw new Exception("Password not similar to the repetition!");
		}

		if (password.length() < 5) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password too short!");
			throw new Exception("Password too short!");
		}

		account = new Account();
		account.setFirstName(firstName);
		account.setLastName(name);
		account.setUsername(accountName);
		account.setEmail(mail);

		validateAccount(account);

		checkAccount = accountRepository.findAccountByUsername(accountName);

		if (checkAccount != null) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "UserName does already exists!");
			throw new Exception("UserName does already exists!");
		}

		hash = HashHelper.calcSHA256(password);
		account.setPassword(hash);


		ArrayList<Group> groups = new ArrayList<Group>();
		groups.add(Group.ADMINISTRATOR);
		account.setGroups(groups);


		accountRepository.create(account);
		return account;
	}

	@RolesAllowed({"ADMINISTRATOR", "USER"})
	@Override
	public void changePassword(String mail, String oldPassword, String password, String passwordRepeat)
			throws Exception {

		Account account;
		String hash = "";

		if (oldPassword.equals("") || password.equals("") || passwordRepeat.equals("")) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Incomplete passwordsfields!");
			throw new Exception("Incomplete passwordsfields!");
		}
		if (!password.equals(passwordRepeat)) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password not similar to the repetition!");
			throw new Exception("Password not similar to the repetition!");
		}

		if (password.length() < 5) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Password too short!");
			throw new Exception("Password too short!");
		}

		account = getAccount(mail);
		if (account == null) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "User not found!");
			throw new Exception("User not found!");
		}

		hash = HashHelper.calcSHA256(oldPassword);

		if (!account.getPassword().equals(hash)) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Invalid password!");
			throw new Exception("Invalid password!");
		}


		hash = HashHelper.calcSHA256(password);

		account.setPassword(hash);
		accountRepository.edit(account);
	}

	@Override
	@RolesAllowed({"ADMINISTRATOR", "USER"})
	public Account getAccount(String mail) {
		return accountRepository.find(mail);
	}

	@Override
	@RolesAllowed({"ADMINISTRATOR", "USER"})
	public Account getAccountByUsername(String username) {
		return accountRepository.findAccountByUsername(username);
	}

	private void validateAccount(Account account) throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);

		String errors = "";
		if (!constraintViolations.isEmpty()) {
			for (ConstraintViolation<Account> violation : constraintViolations) {
				LOG.log(this.getClass().getName(), Level.SEVERE, "Validation Error \"{0}\" at value \"{1}\" and attribute \"{2}\"", new Object[]{violation.getMessage(), violation.getInvalidValue(), violation.getPropertyPath().toString()});
				errors = errors + "Validation Error: " + violation.getMessage() + " at value: " + violation.getInvalidValue() + "\n";
			}
			throw new Exception("ValidationException: User has invalid attributes." + errors);
		}
	}
}
