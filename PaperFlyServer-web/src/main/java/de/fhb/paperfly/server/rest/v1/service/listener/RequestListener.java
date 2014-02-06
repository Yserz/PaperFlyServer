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
import javax.servlet.http.HttpSession;

/**
 * This listener listens for new requests and touches the session.
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
			HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
			HttpSession session = request.getSession(false);
			if (session != null) {
				System.out.println("last session access: " + new Date(session.getLastAccessedTime()));
			} else {
				System.out.println("Session is null!");
			}

		} catch (Exception e) {
			System.out.println("could not access session!");
			e.printStackTrace();
		}

	}
}
