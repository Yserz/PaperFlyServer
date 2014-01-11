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
package de.fhb.paperfly.server.account.repository;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.base.repository.AbstractRepository;
import de.fhb.paperfly.server.logging.interceptor.RepositoryLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.util.Settings;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class provides specialized methods for database operations related to
 * accounts.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
public class AccountRepository extends AbstractRepository<Account> {

	@EJB
	private LoggingServiceLocal LOG;
	@PersistenceContext(unitName = Settings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public AccountRepository() {
		super(Account.class);
	}

	@PostConstruct
	private void init() {
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	/**
	 * This method offers the ability to find accounts with thier username
	 * attribute.
	 *
	 * @param username The username the account belongs to
	 * @return The account or null if the account doesnt exists
	 */
	public Account findByUsername(String username) {
		Account acc = null;

		//TODO may handle exceptions in the next higher layer.
		try {
			acc = em.createNamedQuery("Account.findByUsername", Account.class).setParameter("username", username).getSingleResult();
		} catch (NonUniqueResultException | NoResultException e) {
			LOG.log(this.getClass().getName(), Level.INFO, "Exception: " + e.getMessage(), e);
		}
		return acc;
	}

	/**
	 * This method searches for an accounts username by a query.
	 *
	 * @param keyword The string to search in all usernames of all accounts
	 * @return A list of accounts
	 */
	public List<Account> searchByUsername(String keyword) {
		return em.createNamedQuery("Account.searchByUsername", Account.class)
				.setParameter("keyword", "%" + keyword + "%").getResultList();

	}
}
