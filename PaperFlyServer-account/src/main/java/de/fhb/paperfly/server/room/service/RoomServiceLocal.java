package de.fhb.paperfly.server.room.service;

import de.fhb.paperfly.server.room.entity.Room;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author MacYser
 */
@Local
public interface RoomServiceLocal {

	Room createRoom(Room room);

	void removeRoom(Room room);

	Room editRoom(Room room);

	Room getRoom(Long roomID);

	Room getRoomByRoomName(String roomName);

	List<Room> getRoomList();
}
