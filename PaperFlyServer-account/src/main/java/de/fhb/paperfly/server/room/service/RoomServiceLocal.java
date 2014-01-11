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

	/**
	 * Method to create a new room.
	 *
	 * @param room The room to create.
	 * @return The created room.
	 */
	Room createRoom(Room room);

	/**
	 * Method to remove a room.
	 *
	 * @param room The room to remove.
	 */
	void removeRoom(Room room);

	/**
	 * Method to edit a room.
	 *
	 * @param room The Room with the new data.
	 * @return The edited room.
	 */
	Room editRoom(Room room);

	/**
	 * Method to get a room by the ID.
	 *
	 * @param roomID The rooms ID.
	 * @return The room found by ID or null if the room does not exists.
	 */
	Room getRoom(Long roomID);

	/**
	 * Method to get a room by the name.
	 *
	 * @param roomName The rooms name.
	 * @return The room found by name or null if the room does not exists.
	 */
	Room getRoomByRoomName(String roomName);

	/**
	 * Method to get a list of all rooms.
	 *
	 * @return A list of all rooms.
	 */
	List<Room> getRoomList();
}
