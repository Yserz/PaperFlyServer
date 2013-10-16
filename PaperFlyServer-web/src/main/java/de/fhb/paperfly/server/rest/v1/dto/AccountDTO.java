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
package de.fhb.paperfly.server.rest.v1.dto;

import de.fhb.paperfly.server.account.entity.Group;
import de.fhb.paperfly.server.rest.v1.base.BaseDTO;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class AccountDTO extends BaseDTO {

	private String email;
	@NotNull
	@Size(min = 1, max = 255)
	private String username;
	@Size(min = 6, max = 255)
	private String password;
	@NotNull
	private List<String> groups;
	@NotNull
	@Size(min = 1, max = 255)
	private String lastName;
	@NotNull
	@Size(min = 1, max = 255)
	private String firstName;

	public AccountDTO() {
	}

	public AccountDTO(String email, String username, String password, List<String> groups, String lName, String fName, Date birthday) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.groups = groups;
		this.lastName = lName;
		this.firstName = fName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lName) {
		this.lastName = lName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String fName) {
		this.firstName = fName;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 47 * hash + Objects.hashCode(this.email);
		hash = 47 * hash + Objects.hashCode(this.username);
		hash = 47 * hash + Objects.hashCode(this.password);
		hash = 47 * hash + Objects.hashCode(this.groups);
		hash = 47 * hash + Objects.hashCode(this.lastName);
		hash = 47 * hash + Objects.hashCode(this.firstName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AccountDTO other = (AccountDTO) obj;
		if (!Objects.equals(this.email, other.email)) {
			return false;
		}
		if (!Objects.equals(this.username, other.username)) {
			return false;
		}
		if (!Objects.equals(this.password, other.password)) {
			return false;
		}
		if (!Objects.equals(this.groups, other.groups)) {
			return false;
		}
		if (!Objects.equals(this.lastName, other.lastName)) {
			return false;
		}
		if (!Objects.equals(this.firstName, other.firstName)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return super.toString() + " AccountDTO{" + "email=" + email + ", username=" + username + ", password=" + password + ", groups=" + groups + ", lName=" + lastName + ", fName=" + firstName + '}';
	}
}
