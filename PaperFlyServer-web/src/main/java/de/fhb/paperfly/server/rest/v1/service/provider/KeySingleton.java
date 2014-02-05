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

	public static KeySingleton getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {

		private static final KeySingleton INSTANCE = new KeySingleton();
	}

	public synchronized DefaultOAuthProvider.Consumer getConsumer(String consumerKey) {
		System.out.println("getConsumer:");
		for (Map.Entry<String, DefaultOAuthProvider.Consumer> entry : consumerByConsumerKey.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue().getPrincipal());
		}
		return consumerByConsumerKey.get(consumerKey);
	}

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

	public synchronized DefaultOAuthProvider.Consumer removeConsumer(String consumerKey) {
		System.out.println("removeConsumer:");
		for (Map.Entry<String, DefaultOAuthProvider.Consumer> entry : consumerByConsumerKey.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue().getPrincipal());
		}
		return consumerByConsumerKey.remove(consumerKey);
	}

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
