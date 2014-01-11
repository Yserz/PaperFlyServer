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
package de.fhb.paperfly.server.room.service;

import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.room.entity.Coordinate;
import de.fhb.paperfly.server.room.entity.Room;
import de.fhb.paperfly.server.room.respository.RoomRepository;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.*;

/**
 * This class provides all business operations on rooms.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Stateless
@Startup
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RoomService implements RoomServiceLocal {

	@EJB
	private LoggingServiceLocal LOG;
	@EJB
	private RoomRepository roomRepository;

	public RoomService() {
	}

	@PostConstruct
	private void init() {
		//TODO create some rooms
		createRoom(new Room(null, "23" + "-" + "WWZ", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "WWZ"));
		createRoom(new Room(null, "027" + "-" + "INFZ", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "INFZ"));
		createRoom(new Room(null, "202" + "-" + "IWZ", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "IWZ"));
		createRoom(new Room(null, "Mensa", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "MENSA"));
		createRoom(new Room(null, "Audimax", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "AUDIMAX"));
		createRoom(new Room(null, "Bibliothek", new Coordinate(0./*longlitutde*/, 0./*latitude*/), "BIBLIOTHEK"));
	}

	@Override
	public Room createRoom(Room room) {
		return roomRepository.create(room);
	}

	@Override
	public void removeRoom(Room room) {
	}

	@Override
	public Room editRoom(Room room) {
		return null;
	}

	@Override
	public Room getRoom(Long roomID) {
		return roomRepository.find(roomID);
	}

	@Override
	public Room getRoomByRoomName(String roomName) {
		return roomRepository.findByRoomName(roomName);
	}

	@Override
	public List<Room> getRoomList() {
		return roomRepository.findAll();
	}
}
