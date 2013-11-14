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
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.persistence.annotations.JoinFetch;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Entity
@Cacheable(false)
@Getter
@Setter
@ToString(exclude = {"password"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({})
public class Credential extends BaseEntity {

	@Id
	@OneToOne(optional = false)
	@JoinColumn(name = "EMAIL")
	private Account account;
	@Size(min = 6, max = 255)
	private String password;
	@ElementCollection(targetClass = Group.class)
	@CollectionTable(name = "ACCOUNT_GROUP", joinColumns =
			@JoinColumn(name = "ACCOUNT", nullable = false),
			uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ACCOUNT", "GROUPS"})
	})
	@Enumerated(EnumType.STRING)
	@Column(name = "GROUPS", nullable = false)
	@JoinFetch
	private List<Group> groups;
}
