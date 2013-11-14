/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.rest.v1.service.provider;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.spi.OAuthConsumer;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import com.sun.jersey.oauth.server.spi.OAuthToken;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author MacYser
 */
@Provider
public class DefaultOAuthProvider implements OAuthProvider {

	private static final ConcurrentHashMap<String, Consumer> consumerByConsumerKey = new ConcurrentHashMap<String, Consumer>();
	private static final ConcurrentHashMap<String, Token> accessTokenByTokenString = new ConcurrentHashMap<String, Token>();
	private static final ConcurrentHashMap<String, Token> requestTokenByTokenString = new ConcurrentHashMap<String, Token>();
	private static final ConcurrentHashMap<String, String> verifierByTokenString = new ConcurrentHashMap<String, String>();

	@Override
	public Consumer getConsumer(String consumerKey) {
		return consumerByConsumerKey.get(consumerKey);
	}

	/**
	 * Registers a new consumer.
	 *
	 * @param owner Identifier of the owner that registers the consumer (user ID
	 * or similar).
	 * @param attributes Additional attributes (name-values pairs - to store
	 * additional information about the consumer, such as name, URI,
	 * description, etc.)
	 * @return Consumer object for the newly registered consumer.
	 */
	public Consumer registerConsumer(String owner, Principal principal, MultivaluedMap<String, String> attributes) {
		Consumer c = new Consumer(newUUIDString(), newUUIDString(), owner, principal, attributes);
		consumerByConsumerKey.put(c.getKey(), c);
		return c;
	}

	/**
	 * Returns a set of consumers registered by a given owner.
	 *
	 * @param owner Identifier of the owner that registered the consumers to be
	 * retrieved.
	 * @return consumers registered by the owner.
	 */
	public Set<Consumer> getConsumers(String owner) {
		Set<Consumer> result = new HashSet<Consumer>();
		for (Consumer consumer : consumerByConsumerKey.values()) {
			if (consumer.getOwner().equals(owner)) {
				result.add(consumer);
			}
		}
		return result;
	}

	/**
	 * Returns a list of access tokens authorized with the supplied principal
	 * name.
	 *
	 * @param principalName Principal name for which to retrieve the authorized
	 * tokens.
	 * @return authorized access tokens.
	 */
	public Set<Token> getAccessTokens(String principalName) {
		Set<Token> tokens = new HashSet<Token>();
		for (Token token : accessTokenByTokenString.values()) {
			if (principalName.equals(token.getPrincipal().getName())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	/**
	 * Authorizes a request token for given principal and roles and returns
	 * verifier.
	 *
	 * @param token Request token to authorize.
	 * @param userPrincipal User principal to authorize the token for.
	 * @param roles Set of roles to authorize the token for.
	 * @return OAuth verifier value for exchanging this token for an access
	 * token.
	 */
	public String authorizeToken(Token token, Principal userPrincipal, Set<String> roles) {
		Token authorized = token.authorize(userPrincipal, roles);
		requestTokenByTokenString.put(token.getToken(), authorized);
		String verifier = newUUIDString();
		verifierByTokenString.put(token.getToken(), verifier);
		return verifier;
	}

	/**
	 * Checks if the supplied token is authorized for a given principal name and
	 * if so, revokes the authorization.
	 *
	 * @param token Access token to revoke the authorization for.
	 * @param principalName Principal name the token is currently authorized
	 * for.
	 */
	public void revokeAccessToken(String token, String principalName) {
		Token t = (Token) getAccessToken(token);
		if (t != null && t.getPrincipal().getName().equals(principalName)) {
			accessTokenByTokenString.remove(token);
		}
	}

	/**
	 * Generates a new non-guessable random string (used for token/customer
	 * strings, secrets and verifier.
	 *
	 * @return Random UUID string.
	 */
	protected String newUUIDString() {
		String tmp = UUID.randomUUID().toString();
		return tmp.replaceAll("-", "");
	}

	@Override
	public Token getRequestToken(String token) {
		return requestTokenByTokenString.get(token);
	}

	@Override
	public OAuthToken newRequestToken(String consumerKey, String callbackUrl, Map<String, List<String>> attributes) {
		Token rt = new Token(newUUIDString(), newUUIDString(), consumerKey, callbackUrl, attributes);
		requestTokenByTokenString.put(rt.getToken(), rt);
		return rt;
	}

	@Override
	public OAuthToken newAccessToken(OAuthToken requestToken, String verifier) {
		if (verifier == null || !verifier.equals(verifierByTokenString.remove(requestToken.getToken()))) {
			return null;
		}
		Token token = requestToken == null ? null : requestTokenByTokenString.remove(requestToken.getToken());
		if (token == null) {
			return null;
		}
		Token at = new Token(newUUIDString(), newUUIDString(), token);
		accessTokenByTokenString.put(at.getToken(), at);
		return at;
	}

	@Override
	public OAuthToken getAccessToken(String token) {
		return accessTokenByTokenString.get(token);
	}

	/**
	 * Simple read-only implementation of {@link OAuthConsumer}.
	 */
	public static class Consumer implements OAuthConsumer {

		private final String key;
		private final String secret;
		private final String owner;
		private final Principal principal;
		private final MultivaluedMap<String, String> attribs;

		private Consumer(String key, String secret, String owner, Principal principal, Map<String, List<String>> attributes) {
			this.key = key;
			this.secret = secret;
			this.owner = owner;
			this.principal = principal;
			this.attribs = newImmutableMultiMap(attributes);
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getSecret() {
			return secret;
		}

		/**
		 * Returns identifier of owner of this consumer - i.e. who registered
		 * the consumer.
		 *
		 * @return consumer owner
		 */
		public String getOwner() {
			return owner;
		}

		/**
		 * Returns additional attributes associated with the consumer (e.g.
		 * name, URI, description, etc.)
		 *
		 * @return name-values pairs of additional attributes
		 */
		public MultivaluedMap<String, String> getAttributes() {
			return attribs;
		}

		@Override
		public Principal getPrincipal() {
			return principal;
		}

		@Override
		public boolean isInRole(String role) {
			return attribs.get("roles").contains(role);
		}
	}

	/**
	 * Simple immutable implementation of {@link OAuthToken}.
	 *
	 */
	public class Token implements OAuthToken {

		private final String token;
		private final String secret;
		private final String consumerKey;
		private final String callbackUrl;
		private final Principal principal;
		private final Set<String> roles;
		private final MultivaluedMap<String, String> attribs;

		protected Token(String token, String secret, String consumerKey, String callbackUrl,
				Principal principal, Set<String> roles, MultivaluedMap<String, String> attributes) {
			this.token = token;
			this.secret = secret;
			this.consumerKey = consumerKey;
			this.callbackUrl = callbackUrl;
			this.principal = principal;
			this.roles = roles;
			this.attribs = attributes;
		}

		public Token(String token, String secret, String consumerKey, String callbackUrl, Map<String, List<String>> attributes) {
			this(token, secret, consumerKey, callbackUrl, null, Collections.<String>emptySet(),
					newImmutableMultiMap(attributes));
		}

		public Token(String token, String secret, Token requestToken) {
			this(token, secret, requestToken.getConsumer().getKey(), null,
					requestToken.principal, requestToken.roles, ImmutableMultiMap.EMPTY);
		}

		@Override
		public String getToken() {
			return token;
		}

		@Override
		public String getSecret() {
			return secret;
		}

		@Override
		public OAuthConsumer getConsumer() {
			return DefaultOAuthProvider.this.getConsumer(consumerKey);
		}

		@Override
		public MultivaluedMap<String, String> getAttributes() {
			return attribs;
		}

		@Override
		public Principal getPrincipal() {
			return principal;
		}

		@Override
		public boolean isInRole(String role) {
			return roles.contains(role);
		}

		/**
		 * Returns callback URL for this token (applicable just to request
		 * tokens)
		 *
		 * @return callback url
		 */
		public String getCallbackUrl() {
			return callbackUrl;
		}

		/**
		 * Authorizes this token - i.e. generates a clone with principal and
		 * roles set to the passed values.
		 *
		 * @param principal Principal to add to the token.
		 * @param roles Roles to add to the token.
		 * @return Cloned token with the principal and roles set.
		 */
		protected Token authorize(Principal principal, Set<String> roles) {
			return new Token(token, secret, consumerKey, callbackUrl, principal, roles == null ? Collections.<String>emptySet() : new HashSet<String>(roles), attribs);
		}
	}

	protected static MultivaluedMap<String, String> newImmutableMultiMap(Map<String, List<String>> source) {
		if (source == null) {
			return ImmutableMultiMap.EMPTY;
		}
		return new ImmutableMultiMap(source);
	}

	private static class ImmutableMultiMap extends MultivaluedMapImpl {

		public static final ImmutableMultiMap EMPTY = new ImmutableMultiMap();

		private ImmutableMultiMap() {
		}

		ImmutableMultiMap(Map<String, List<String>> source) {
			for (Map.Entry<String, List<String>> e : source.entrySet()) {
				super.put(e.getKey(), e.getValue() == null ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<String>(e.getValue())));
			}
		}

		@Override
		public List<String> put(String k, List<String> v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, List<String>>> entrySet() {
			return Collections.unmodifiableSet(super.entrySet());
		}

		@Override
		public Set<String> keySet() {
			return Collections.unmodifiableSet(super.keySet());
		}

		@Override
		public List<String> remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends List<String>> map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<List<String>> values() {
			return Collections.unmodifiableCollection(super.values());
		}
	}
}
