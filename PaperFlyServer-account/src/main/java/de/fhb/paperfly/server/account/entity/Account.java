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
package de.fhb.paperfly.server.account.entity;

import de.fhb.paperfly.server.base.entity.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This is an entity for an account.
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
@NamedQueries({
	@NamedQuery(name = "Account.findByUsername", query = "SELECT a FROM Account a WHERE a.username = :username"),
	@NamedQuery(name = "Account.searchByUsername", query = "SELECT a FROM Account a WHERE UPPER(a.username) LIKE UPPER(:keyword)")
})
public class Account extends BaseEntity {

	@Id
	private String email;
	@NotNull
	@Size(min = 1, max = 255)
	@Column(unique = true)
	private String username;
	@NotNull
	@Size(min = 1, max = 255)
	private String lastName;
	@NotNull
	@Size(min = 1, max = 255)
	private String firstName;
	@ElementCollection
	private Set<String> friendListUsernames = new HashSet<>();
	private Status status;
}
