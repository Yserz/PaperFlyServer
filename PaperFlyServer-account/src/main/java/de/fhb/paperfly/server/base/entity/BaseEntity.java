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
package com.welovecoding.base.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
//@Interceptors(EntityLoggerInterceptor.class)
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastModified;
	private boolean enabled;

	public BaseEntity() {
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (this.created != null ? this.created.hashCode() : 0);
		hash = 37 * hash + (this.lastModified != null ? this.lastModified.hashCode() : 0);
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
		final BaseEntity other = (BaseEntity) obj;
		if (this.created != other.created && (this.created == null || !this.created.equals(other.created))) {
			return false;
		}
		if (this.lastModified != other.lastModified && (this.lastModified == null || !this.lastModified.equals(other.lastModified))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BaseEntity{" + "created=" + created + ", lastModified=" + lastModified + '}';
	}
}
