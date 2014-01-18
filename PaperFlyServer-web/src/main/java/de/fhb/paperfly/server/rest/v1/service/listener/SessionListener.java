/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.listener;

import de.fhb.paperfly.server.account.entity.Account;
import de.fhb.paperfly.server.account.entity.Status;
import de.fhb.paperfly.server.account.service.AccountServiceLocal;
import de.fhb.paperfly.server.rest.v1.dto.AccountDTO;
import de.fhb.paperfly.server.rest.v1.service.PaperFlyRestService;
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

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("Session '" + se.getSession().getId() + "' created!");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		try {
			System.out.println("isNewSession: " + se.getSession().isNew());
			System.out.println("Session '" + se.getSession().getId() + "'!");
			String mail = (String) se.getSession().getAttribute("mail");
			Account myAccount = accountService.getAccountByMail(mail);
			myAccount.setStatus(Status.OFFLINE);
			accountService.editAccount(myAccount);
		} catch (Exception e) {
			System.out.println("Error on setting Account as OFFLINE!");
		}
		System.out.println("Session '" + se.getSession().getId() + "'destroyed!");
	}
}
