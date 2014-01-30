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
package de.fhb.paperfly.server.room.respository;

import de.fhb.paperfly.server.base.repository.AbstractRepository;
import de.fhb.paperfly.server.logging.interceptor.RepositoryLoggerInterceptor;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.util.Settings;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 * This class provides specialized methods for database operations related to
 * Rooms.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
public class RoomRepository extends AbstractRepository<Room> {

	@PersistenceContext(unitName = Settings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public RoomRepository() {
		super(Room.class);
	}

	@PostConstruct
	private void init() {
		LOG.setLoggerLoggingLevel(this.getClass().getName(), Level.SEVERE);
		LOG.setLoggerLoggingLevel(super.getClass().getName(), Level.SEVERE);
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
	 * finds a room by the given room name.
	 *
	 * @param roomName The name of the room to search for
	 * @return The room or null if the room does not exists
	 */
	public Room findByRoomName(String roomName) {
		Room room = null;

		//TODO may handle exceptions in the next higher layer.
		try {
			room = em.createNamedQuery("Room.findByRoomName", Room.class).setParameter("name", roomName).getSingleResult();
		} catch (NonUniqueResultException | NoResultException e) {
			LOG.log(this.getClass().getName(), Level.INFO, "Exception: " + e.getMessage(), e);
		}
		return room;
	}
}
