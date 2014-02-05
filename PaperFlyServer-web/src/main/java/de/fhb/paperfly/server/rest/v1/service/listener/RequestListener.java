/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.listener;

import java.util.Date;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@WebListener
public class RequestListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		//nothing to do
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		try {
			System.out.println("last session access: " + new Date(((HttpServletRequest) sre.getServletRequest()).getSession(false).getLastAccessedTime()));
		} catch (Exception e) {
			System.out.println("could not access session!");
		}

	}
}
