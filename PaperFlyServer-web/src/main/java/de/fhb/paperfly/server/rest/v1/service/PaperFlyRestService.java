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
package de.fhb.paperfly.server.rest.v1.service;

import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.mapping.ToDTOMapper;
import de.fhb.paperfly.server.rest.v1.mapping.ToEntityMapper;
import de.fhb.paperfly.server.rest.v1.service.resources.account.AccountResource;
import de.fhb.paperfly.server.rest.v1.service.resources.account.MyAccountResource;
import de.fhb.paperfly.server.rest.v1.service.resources.auth.AuthResource;
import de.fhb.paperfly.server.rest.v1.service.resources.room.RoomResource;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * This class represents the REST-Endpoint of the application. It routes to the
 * specific REST-Resources. Address:
 * http://localhost:8080/PaperFlyServer-web/rest/v1
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Path("/v1")
@Singleton
//@Interceptors({WebServiceLoggerInterceptor.class})
public class PaperFlyRestService {

	@EJB
	public LoggingServiceLocal LOG;
	public static final String JSON_MEDIA_TYPE = "application/json;charset=utf-8";
	public static ToDTOMapper toDTOMapper;
	public static ToEntityMapper toEntityMapper;
	// Resources
	@EJB
	private AccountResource accountResource;
	@EJB
	private MyAccountResource myAccountResource;
	@EJB
	private AuthResource authResource;
	@EJB
	private RoomResource roomResource;

	public PaperFlyRestService() {
		toDTOMapper = new ToDTOMapper();
		toEntityMapper = new ToEntityMapper();
	}

	@PostConstruct
	private void init() {
	}

	/**
	 * Method to ping the service.
	 *
	 * @title Ping the Service
	 * @summary Pings the Service to test if it is online.
	 * @return Returns an alive-message.
	 */
	@GET
	@Path("ping")
	public String ping(@Context SecurityContext sc) {
		return "alive";
	}

	@Path("auth/")
	public AuthResource getAuthResource() {
		return authResource;
	}

	@Path("account/")
	public AccountResource getAccountResource() {
		return accountResource;
	}

	@Path("myaccount/")
	public MyAccountResource getMyAccountResource() {
		return myAccountResource;
	}

	@Path("room/")
	public RoomResource getRoomResource() {
		return roomResource;
	}

	public LoggingServiceLocal getLOG() {
		return LOG;
	}
}
