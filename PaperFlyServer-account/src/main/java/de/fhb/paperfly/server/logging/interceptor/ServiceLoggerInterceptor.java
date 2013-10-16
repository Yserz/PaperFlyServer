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
package de.fhb.paperfly.server.logging.interceptor;

import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * This Interceptor is invoked in every session-bean-service-class and is
 * responsible for logging genaral information about actual classname methodname
 * and params.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class ServiceLoggerInterceptor {

	@EJB
	private LoggingServiceLocal LOG;

	public ServiceLoggerInterceptor() {
	}

	/**
	 * logging of genaral information about actual classname methodname and
	 * params.
	 *
	 * @param context
	 * @return context proceed
	 * @throws Exception
	 */
	@AroundInvoke
	public Object logCall(InvocationContext context) throws Exception {
		int count = 0;
		// EJB will not be accessable if the Interceptor is called from outside of the welovecoding-backend-module

		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, "---------------------------------------------------------");
		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " + Class: {0}", getPureClassName(context.getMethod().getDeclaringClass()));
		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -    Method: {0}", context.getMethod().getName());

		if (context.getParameters() != null) {
			for (Object object : context.getParameters()) {
				if (object != null) {
					count++;
					LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -       Param {0}: ({1}) {2}", new Object[]{count, getPureClassName(object.getClass()), object});
				} else {
					count++;
					LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -       Param {0}: ({1}) {2}", new Object[]{count, "", object});
				}
			}
		}

		return context.proceed();
	}

	/**
	 * seperates the packagename from the classname and returns the classname.
	 *
	 * @param klasse
	 * @return String
	 */
	private String getPureClassName(Class klasse) {
		String temp = "";

		String classNameWithPackage = klasse.getName();
		String packetname = klasse.getPackage().getName();

		temp = packetname.replaceAll("package ", "");

		temp = classNameWithPackage.replaceAll(temp + ".", "");


		return temp;
	}
}
