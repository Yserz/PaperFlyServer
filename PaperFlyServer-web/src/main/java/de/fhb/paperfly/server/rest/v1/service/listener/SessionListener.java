/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.listener;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.logging.service.LoggingServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@WebListener
public class SessionListener implements HttpSessionListener {

	@EJB
	private AccountServiceLocal accountService;
	@EJB
	private LoggingServiceLocal LOG;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		try {
			LOG.log(this.getClass().getName(), Level.INFO, "Session '" + se.getSession().getId() + "' created!");

		} catch (Exception e) {
		}
		LOG.log(this.getClass().getName(), Level.INFO, "Session created!");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		try {
			LOG.log(this.getClass().getName(), Level.INFO, "isNewSession: " + se.getSession().isNew());
			LOG.log(this.getClass().getName(), Level.INFO, "Session '" + se.getSession().getId() + "'!");
			String mail = (String) se.getSession().getAttribute("mail");
			if (mail != null) {
				Account myAccount = accountService.getAccountByMail(mail);
				myAccount.setStatus(Status.OFFLINE);
				accountService.editAccount(myAccount);

			} else {
				throw new Exception("User already logged out!");
			}

			LOG.log(this.getClass().getName(), Level.INFO, "Session '" + se.getSession().getId() + "'destroyed!");
		} catch (Exception e) {
			LOG.log(this.getClass().getName(), Level.SEVERE, "Error on setting Account as OFFLINE!", e);
		}
		LOG.log(this.getClass().getName(), Level.INFO, "Session destroyed!");
	}
}
