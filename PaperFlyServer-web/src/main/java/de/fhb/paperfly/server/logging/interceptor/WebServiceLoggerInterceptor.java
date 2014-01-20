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
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

/**
 * This Interceptor should be invoked in every session-bean-service-class and is
 * responsible for logging genaral information about actual classname methodname
 * and params.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class WebServiceLoggerInterceptor {

	@EJB
	private LoggingServiceLocal LOG;

	public WebServiceLoggerInterceptor() {
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
		// EJB will not be accessable if the Interceptor is called from outside of the backend-module


		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, "---------------------------------------------------------");
		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " + Class: {0}", getPureClassName(context.getMethod().getDeclaringClass()));
		LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -    Method: {0}", context.getMethod().getName());

		if (context.getParameters() != null) {
			Annotation[][] annos = context.getMethod().getParameterAnnotations();
			Object[] params = context.getParameters();
			for (int i = 0; i < annos.length; i++) {

				for (int j = 0; j < annos[i].length; j++) {
					Annotation annotation = annos[i][j];
					System.out.println("Annotation for Param " + (i + 1) + ": " + annotation.annotationType());
				}

				if (params[i] != null) {
					if (!params[i].toString().contains("org.apache.catalina.connector.RequestFacade")) {
						LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -       Param {0}: ({1}) {2}", new Object[]{i + 1, getPureClassName(params[i].getClass()), params[i]});
					} else {
						LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -       Param {0}: ({1}) {2}", new Object[]{i + 1, "RequestFacade", "org.apache.catalina.connector.RequestFacade"});
					}
				} else {
					LOG.log(context.getMethod().getDeclaringClass().getName(), Level.INFO, " -       Param {0}: ({1}) {2}", new Object[]{i + 1, "", params[i]});
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
		String packetname = "";

		String classNameWithPackage = klasse.getName();

		if (klasse.getPackage() != null) {
			packetname = klasse.getPackage().getName();
		}

		temp = packetname.replaceAll("package ", "");

		temp = classNameWithPackage.replaceAll(temp + ".", "");


		return temp;
	}
}
