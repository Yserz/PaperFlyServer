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

import de.fhb.paperfly.server.account.entity.Credential;
import de.fhb.paperfly.server.base.repository.AbstractRepository;
import de.fhb.paperfly.server.logging.interceptor.RepositoryLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.util.Settings;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class provides specialized methods for database operations related to
 * credentials of an account.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
public class CredentialRepository extends AbstractRepository<Credential> {

	@EJB
	private LoggingServiceLocal LOG;
	@PersistenceContext(unitName = Settings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public CredentialRepository() {
		super(Credential.class);
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
}
