/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.provider;

import de.fhb.paperfly.server.rest.v1.service.provider.DefaultOAuthProvider.Consumer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class saves all consumer keys. Consumer keys are used for
 * authentification on the system.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class KeySingleton {

	public HashMap<String, DefaultOAuthProvider.Consumer> consumerByConsumerKey = new HashMap<String, DefaultOAuthProvider.Consumer>();
	public ConcurrentHashMap<String, DefaultOAuthProvider.Token> accessTokenByTokenString = new ConcurrentHashMap<String, DefaultOAuthProvider.Token>();
	public ConcurrentHashMap<String, DefaultOAuthProvider.Token> requestTokenByTokenString = new ConcurrentHashMap<String, DefaultOAuthProvider.Token>();
	public ConcurrentHashMap<String, String> verifierByTokenString = new ConcurrentHashMap<String, String>();

	private KeySingleton() {
	}

	/**
	 * Gets the instance of the singleton class.
	 *
	 * @return
	 */
	public static KeySingleton getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {

		private static final KeySingleton INSTANCE = new KeySingleton();
	}

	/**
	 * Gets a consumer by the consumerkey.
	 *
	 * @param consumerKey
	 * @return Consumer
	 */
	public synchronized DefaultOAuthProvider.Consumer getConsumer(String consumerKey) {
		StringBuilder log = new StringBuilder("");
		log.append("getConsumer:").append("\n");
		for (Map.Entry<String, DefaultOAuthProvider.Consumer> entry : consumerByConsumerKey.entrySet()) {
			log.append(entry.getKey()).append(" : ").append(entry.getValue().getPrincipal()).append("\n");
		}
		System.out.println(log.toString());
		return consumerByConsumerKey.get(consumerKey);
	}

	/**
	 * Gets the Consumer by mail.
	 *
	 * @param mail
	 * @return Consumer
	 */
	public synchronized DefaultOAuthProvider.Consumer getConsumerByMail(String mail) {

		String key = "";
		for (Map.Entry<String, Consumer> entry : consumerByConsumerKey.entrySet()) {
			String string = entry.getKey();
			Consumer consumer = entry.getValue();
			if (consumer.getPrincipal().toString().equals(mail)) {
				key = string;
			}

		}
		return getConsumer(key);
	}

	/**
	 * Removes a consumer by the consumer key.
	 *
	 * @param consumerKey
	 * @return removed consumer
	 */
	public synchronized DefaultOAuthProvider.Consumer removeConsumer(String consumerKey) {
		StringBuilder log = new StringBuilder("");
		log.append("removeConsumer:").append("\n");
		for (Map.Entry<String, DefaultOAuthProvider.Consumer> entry : consumerByConsumerKey.entrySet()) {
			log.append(entry.getKey()).append(" : ").append(entry.getValue().getPrincipal()).append("\n");
		}
		System.out.println(log.toString());
		return consumerByConsumerKey.remove(consumerKey);
	}

	/**
	 * Removes a consumer by mail.
	 *
	 * @param mail
	 * @return removed consumer
	 */
	public synchronized DefaultOAuthProvider.Consumer removeConsumerByMail(String mail) {
		String key = "";
		for (Map.Entry<String, Consumer> entry : consumerByConsumerKey.entrySet()) {
			String string = entry.getKey();
			Consumer consumer = entry.getValue();
			if (consumer.getPrincipal().toString().equals(mail)) {
				key = string;
			}

		}

		return removeConsumer(key);
	}
}
