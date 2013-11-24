///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.fhb.paperfly.server;
//
//import com.sun.jersey.api.core.ResourceConfig;
//import com.sun.jersey.oauth.server.NonceManager;
//import com.sun.jersey.oauth.server.OAuthException;
//import com.sun.jersey.oauth.server.OAuthServerRequest;
//import com.sun.jersey.oauth.server.spi.OAuthConsumer;
//import com.sun.jersey.oauth.server.spi.OAuthProvider;
//import com.sun.jersey.oauth.server.spi.OAuthToken;
//import com.sun.jersey.oauth.signature.OAuthParameters;
//import com.sun.jersey.oauth.signature.OAuthSecrets;
//import com.sun.jersey.oauth.signature.OAuthSignature;
//import com.sun.jersey.oauth.signature.OAuthSignatureException;
//import com.sun.jersey.spi.container.ContainerRequest;
//import de.fhb.paperfly.server.rest.v1.service.context.OAuthSecurityContext;
//import static de.fhb.paperfly.server.rest.v1.service.filter.OAuthServerFilter.FEATURE_NO_FAIL;
//import static de.fhb.paperfly.server.rest.v1.service.filter.OAuthServerFilter.PROPERTY_GC_PERIOD;
//import static de.fhb.paperfly.server.rest.v1.service.filter.OAuthServerFilter.PROPERTY_IGNORE_PATH_PATTERN;
//import static de.fhb.paperfly.server.rest.v1.service.filter.OAuthServerFilter.PROPERTY_MAX_AGE;
//import static de.fhb.paperfly.server.rest.v1.service.filter.OAuthServerFilter.PROPERTY_REALM;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.regex.Pattern;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.Response;
//
///**
// *
// * @author MacYser
// */
//public class OAuthFilter implements Filter {
//
//	/**
//	 * OAuth realm. Default is set to "default".
//	 */
//	public static final String PROPERTY_REALM = "com.sun.jersey.config.property.oauth.realm";
//	/**
//	 * Property that can be set to a regular expression used to match the path
//	 * (relative to the base URI) this filter should not be applied to.
//	 */
//	public static final String PROPERTY_IGNORE_PATH_PATTERN = "com.sun.jersey.config.property.oauth.ignorePathPattern";
//	/**
//	 * Can be set to max. age (in milliseconds) of nonces that should be tracked
//	 * (default = 300000 ms = 5 min).
//	 */
//	public static final String PROPERTY_MAX_AGE = "com.sun.jersey.config.property.oauth.maxAge";
//	/**
//	 * Property that can be set to frequency of collecting nonces exceeding max.
//	 * age (default = 100 = every 100 requests).
//	 */
//	public static final String PROPERTY_GC_PERIOD = "com.sun.jersey.config.property.oauth.gcPeriod";
//	/**
//	 * If set to true makes the correct OAuth authentication optional - i.e.
//	 * instead of returning the appropriate status code
//	 * ({@link Response.Status#BAD_REQUEST} or
//	 * {@link Response.Status#UNAUTHORIZED}) the filter will ignore this request
//	 * (as if it was not authenticated) and let the web application deal with
//	 * it.
//	 */
//	public static final String FEATURE_NO_FAIL = "com.sun.jersey.config.feature.oauth.noFail";
//	/**
//	 * OAuth Server
//	 */
//	private final OAuthProvider provider;
//	/**
//	 * Manages and validates incoming nonces.
//	 */
//	private final NonceManager nonces;
//	/**
//	 * Maximum age (in milliseconds) of timestamp to accept in incoming
//	 * messages.
//	 */
//	private final int maxAge;
//	/**
//	 * Average requests to process between nonce garbage collection passes.
//	 */
//	private final int gcPeriod;
//	/**
//	 * Value to return in www-authenticate header when 401 response returned.
//	 */
//	private final String wwwAuthenticateHeader;
//	/**
//	 * OAuth protocol versions that are supported.
//	 */
//	private final Set<String> versions;
//	/**
//	 * Regular expression pattern for path to ignore.
//	 */
//	private final Pattern ignorePathPattern;
//	private final boolean optional;
//
//	public OAuthFilter(@Context ResourceConfig rc, @Context OAuthProvider provider) {
//		this.provider = provider;
//
//		// establish supported OAuth protocol versions
//		HashSet<String> v = new HashSet<String>();
//		v.add(null);
//		v.add("1.0");
//		versions = Collections.unmodifiableSet(v);
//
//		// optional initialization parameters (defaulted)
//		String realm = defaultInitParam(rc, PROPERTY_REALM, "PaperFlyRealm");//"default");
//		maxAge = intValue(defaultInitParam(rc, PROPERTY_MAX_AGE, "300000")); // 5 minutes
//		gcPeriod = intValue(defaultInitParam(rc, PROPERTY_GC_PERIOD, "100")); // every 100 on average
//		ignorePathPattern = pattern(defaultInitParam(rc, PROPERTY_IGNORE_PATH_PATTERN, null)); // no pattern
//		optional = rc.getFeature(FEATURE_NO_FAIL);
////		optional = true;
//
//		nonces = new NonceManager(maxAge, gcPeriod);
//
//		// www-authenticate header for the life of the object
//		wwwAuthenticateHeader = "OAuth realm=\"" + realm + "\"";
//	}
//
//	/**
//	 *
//	 * @param request The servlet request we are processing
//	 * @param response The servlet response we are creating
//	 * @param chain The filter chain we are processing
//	 *
//	 * @exception IOException if an input/output error occurs
//	 * @exception ServletException if a servlet error occurs
//	 */
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response,
//			FilterChain chain)
//			throws IOException, ServletException {
//
//		HttpServletRequest req = (HttpServletRequest)request;
//
//		try {
//			System.out.println("Calling OAuthFilter");
//			// do not filter requests that do not use OAuth authentication
//			String authHeader = req.getHeader(OAuthParameters.AUTHORIZATION_HEADER);
//			if (authHeader == null || !authHeader.toUpperCase().startsWith(OAuthParameters.SCHEME.toUpperCase())) {
//				System.out.println("...No OAuth Header found");
//				throw new Exception();
//			}
//
//			// do not filter if the request path matches pattern to ignore
//			if (match(ignorePathPattern, req.getPathInfo())) {
//				System.out.println("...Requested path is irgnored because of configured ignorePattern");
//				throw new Exception();
//			}
//
//			OAuthSecurityContext sc = null;
//
//			try {
//				sc = getSecurityContext(request);
//			} catch (OAuthException e) {
//				if (optional) {
//					throw new Exception();
//				} else {
//					System.out.println("...OAuth failed");
//					throw new WebApplicationException(e.toResponse());
//				}
//			}
//			System.out.println("...setting OAuth securityContext");
//			req.setSecurityContext(sc);
//		} catch (Exception e) {
//		}
//
//		chain.doFilter(request, response);
//
//	}
//
//	/**
//	 * Destroy method for this filter
//	 */
//	public void destroy() {
//	}
//
//	/**
//	 * Init method for this filter
//	 */
//	public void init(FilterConfig filterConfig) {
//	}
//
//	private OAuthSecurityContext getSecurityContext(ServletRequest request) throws OAuthException {
//		System.out.println("...getSecurityContext");
//		OAuthServerRequest osr = new OAuthServerRequest(request);
//		OAuthParameters params = new OAuthParameters().readRequest(osr);
//
//		// apparently not signed with any OAuth parameters; unauthorized
//		if (params.size() == 0) {
//			System.out.println("...no OAuth parameters");
//			throw newUnauthorizedException();
//		}
//
//		// get required OAuth parameters
//		String consumerKey = requiredOAuthParam(params.getConsumerKey());
//		String token = params.getToken();
//		String timestamp = requiredOAuthParam(params.getTimestamp());
//		String nonce = requiredOAuthParam(params.getNonce());
//
//		// enforce other supported and required OAuth parameters
//		requiredOAuthParam(params.getSignature());
//		supportedOAuthParam(params.getVersion(), versions);
//
//		// retrieve secret for consumer key
//		OAuthConsumer consumer = provider.getConsumer(consumerKey);
//		if (consumer == null) {
//			System.out.println("...no OAuth consumer found");
//			throw newUnauthorizedException();
//		}
//
//		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(consumer.getSecret());
//		OAuthSecurityContext sc;
//		String nonceKey;
//
//		if (token == null) {
//			if (consumer.getPrincipal() == null) {
//				throw newUnauthorizedException();
//			}
//			nonceKey = "c:" + consumerKey;
//			sc = new OAuthSecurityContext(consumer, request.isSecure());
//		} else {
//			OAuthToken accessToken = provider.getAccessToken(token);
//			if (accessToken == null) {
//				throw newUnauthorizedException();
//			}
//
//			OAuthConsumer atConsumer = accessToken.getConsumer();
//			if (atConsumer == null || !consumerKey.equals(atConsumer.getKey())) {
//				throw newUnauthorizedException();
//			}
//
//			nonceKey = "t:" + token;
//			secrets.tokenSecret(accessToken.getSecret());
//			sc = new OAuthSecurityContext(accessToken, request.isSecure());
//		}
//
//		if (!verifySignature(osr, params, secrets)) {
//			throw newUnauthorizedException();
//		}
//
//		if (!nonces.verify(nonceKey, timestamp, nonce)) {
//			throw newUnauthorizedException();
//		}
//
//		return sc;
//	}
//
//	private static String defaultInitParam(ResourceConfig config, String name, String value) {
//		String v = (String) config.getProperty(name);
//		if (v == null || v.length() == 0) {
//			v = value;
//		}
//		return v;
//	}
//
//	private static int intValue(String value) {
//		try {
//			return Integer.valueOf(value);
//		} catch (NumberFormatException nfe) {
//			return -1;
//		}
//	}
//
//	private static String requiredOAuthParam(String value) throws OAuthException {
//		if (value == null) {
//			throw newBadRequestException();
//		}
//		return value;
//	}
//
//	private static String supportedOAuthParam(String value, Set<String> set) throws OAuthException {
//		if (!set.contains(value)) {
//			throw newBadRequestException();
//		}
//		return value;
//	}
//
//	private static Pattern pattern(String p) {
//		if (p == null) {
//			return null;
//		}
//		return Pattern.compile(p);
//	}
//
//	private static boolean match(Pattern pattern, String value) {
//		return (pattern != null && value != null && pattern.matcher(value).matches());
//	}
//
//	private static boolean verifySignature(OAuthServerRequest osr,
//			OAuthParameters params, OAuthSecrets secrets) {
//		try {
//			return OAuthSignature.verify(osr, params, secrets);
//		} catch (OAuthSignatureException ose) {
//			throw newBadRequestException();
//		}
//	}
//
//	private static OAuthException newBadRequestException() throws OAuthException {
//		return new OAuthException(Response.Status.BAD_REQUEST, null);
//	}
//
//	private OAuthException newUnauthorizedException() throws OAuthException {
//		return new OAuthException(Response.Status.UNAUTHORIZED, wwwAuthenticateHeader);
//	}
//}

