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

import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.rest.v1.dto.base.BaseDTO;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a mapped account.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO extends BaseDTO {

	@NotNull
	@Size(min = 1, max = 255)
	private String email;
	@NotNull
	@Size(min = 1, max = 255)
	private String username;
	@NotNull
	@Size(min = 1, max = 255)
	private String lastName;
	@NotNull
	@Size(min = 1, max = 255)
	private String firstName;
	private Status status;
	private Set<String> friendListUsernames;
}
