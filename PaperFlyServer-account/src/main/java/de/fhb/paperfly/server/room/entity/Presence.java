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
package de.fhb.paperfly.server.room.entity;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.base.entity.BaseEntity;
import de.fhb.paperfly.server.room.entity.Room;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This is an entity for a presence. The presence is a timestamp when a account
 * joined a room.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({})
public class Presence extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	@JoinColumn(nullable = false)
	private Account account;
	@ManyToOne
	@JoinColumn(nullable = false)
	private Room room;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date timestampl;
}
