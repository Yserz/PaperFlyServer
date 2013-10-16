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
package de.fhb.paperfly.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class generates a SHA1-, SHA-256 or MD5-hashvalue of a string.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class HashHelper {

	private final static Logger LOG = Logger.getLogger(HashHelper.class.getName());

	/**
	 * This method internally calcutates the hash.
	 *
	 * @param algorithm the algorithm to choose
	 * @param text the String to hash
	 * @return the hashed String in hex format
	 * @throws UnsupportedEncodingException
	 */
	private static String calculateHash(MessageDigest algorithm, String text) throws Exception {

		// get the hash value as byte array
		byte[] hash = null;
		try {
			hash = algorithm.digest(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			LOG.log(Level.SEVERE, null, ex);
			throw new Exception("UnsupportedEncodingException");
		}

		return byteArray2Hex(hash);
	}

	/**
	 * formats the Byte-Array into a Hexvalue.
	 */
	private static String byteArray2Hex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	/**
	 * This method generates the SHA1-hashvalue of a String.
	 *
	 * @param text the String to hash
	 * @return the hashed String
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String calcSHA1(String text) throws Exception {

		MessageDigest sha1 = null;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException ex) {
			LOG.log(Level.SEVERE, null, ex);
			throw new Exception("NoSuchAlgorithmException");
		}

		return calculateHash(sha1, text);
	}

	/**
	 * This method generates the SHA256-hashvalue of a String.
	 *
	 * @param text the String to hash
	 * @return the hashed String
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String calcSHA256(String text) throws Exception {

		MessageDigest sha256 = null;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			LOG.log(Level.SEVERE, null, ex);
			throw new Exception("NoSuchAlgorithmException");
		}

		return calculateHash(sha256, text);
	}

	/**
	 * This method generates the MD5-hashvalue of a String.
	 *
	 * @param text the String to hash
	 * @return the hashed String
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String calcMD5(String text) throws Exception {

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			LOG.log(Level.SEVERE, null, ex);
			throw new Exception("NoSuchAlgorithmException");
		}

		return calculateHash(md5, text);
	}
}
