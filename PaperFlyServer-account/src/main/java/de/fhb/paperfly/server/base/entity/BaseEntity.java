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
package de.fhb.paperfly.server.base.entity;

import de.fhb.paperfly.server.logging.interceptor.EntityLoggerInterceptor;
import java.io.Serializable;
import java.util.Date;
import javax.interceptor.Interceptors;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This is a MappedSuperclass for all Enitities in this system. Every Entity who
 * inherits from this class will have the ability to save the date of creation
 * and the date of the last modfication of its own. This works over an
 * {@link EntityListener}. In Addition the Serializable interface is implemented
 * here.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@MappedSuperclass
@EntityListeners(EntityListener.class)
@Interceptors(EntityLoggerInterceptor.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastModified;
	private boolean enabled;
}
