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
import de.fhb.paperfly.server.account.entity.Credential;
import de.fhb.paperfly.server.account.entity.Group;
import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.account.repository.AccountRepository;
import de.fhb.paperfly.server.account.repository.CredentialRepository;
import de.fhb.paperfly.server.logging.interceptor.ServiceLoggerInterceptor;

import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.util.HashHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.interceptor.Interceptors;
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
@Startup
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors({ServiceLoggerInterceptor.class})
public class AccountService implements AccountServiceLocal, AccountServiceLocalAdmin {

	@EJB
	private LoggingServiceLocal LOG;
	@EJB
	private AccountRepository accountRepository;
	@EJB
	private CredentialRepository credentialRepository;

	public AccountService() {
	}

	@PostConstruct
	private void init() {
		LOG.setLoggerLoggingLevel(this.getClass().getName(), Level.SEVERE);
	}

	@Override
	public Account register(String firstname, String lastname, String username, String mail, String password, String passwordRepeat)
			throws Exception {


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

		Account acc = new Account(mail, username, lastname, firstname, null, Status.OFFLINE);
		validateAccount(acc);
		accountRepository.create(acc);

		List<Group> groups = new ArrayList<>();
		groups.add(Group.USER);
		Credential cred = new Credential(acc, HashHelper.calcSHA256(password), groups);
		credentialRepository.create(cred);

		return acc;
	}

	@Override
	public Account editAccount(Account account) {
		return accountRepository.edit(account);
	}

	@Override
	public List<Account> searchAccountByUsername(String query) {
		return accountRepository.searchByUsername(query);
	}

	@Override
	public Account getAccountByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public Account getAccountByMail(String mail) {
		return accountRepository.find(mail);
	}

	/**
	 * Private method to validate a new account.
	 *
	 * @param account The account to validate.
	 * @throws Exception
	 */
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
