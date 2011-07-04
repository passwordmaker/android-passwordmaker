package org.passwordmaker.android.test;

import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.hashalgos.thirdparty.Md4;

import junit.framework.TestCase;

public class PasswordMakerMd4Test extends TestCase {
	public static String toHexString(byte[]bytes) {
	    StringBuilder sb = new StringBuilder(bytes.length*2);
	    for(byte b: bytes)
	      sb.append(Integer.toHexString(b+0x800).substring(1));
	    return sb.toString();
	}

	
	private String getHashString(String text) throws UnsupportedEncodingException {
		Md4 digest = new Md4();
		digest.update(text.getBytes("UTF-8"));
		byte[] result = digest.digest();
		String resultStr = toHexString(result);
		return resultStr;
	}
	
	/**
	 * From http://en.wikipedia.org/wiki/MD4#MD4_Test_Vectors
	 * MD4 ("") = 31d6cfe0d16ae931b73c59d7e0c089c0
	 * MD4 ("a") = bde52cb31de33e46245e05fbdbd6fb24
	 * MD4 ("abc") = a448017aaf21d8525fc10ae87aa6729d
	 * MD4 ("message digest") = d9130a8164549fe818874806e1c7014b
	 * MD4 ("abcdefghijklmnopqrstuvwxyz") = d79e1c308aa5bbcdeea8ed63df412da9
	 * MD4 ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789") = 043f8582f241db351ce627e153e7f0e4
	 * MD4 ("12345678901234567890123456789012345678901234567890123456789012345678901234567890") = e33b4ddc9c38f2199c3e7b164fcc0536
	 * @throws UnsupportedEncodingException
	 */
	public void testHash() throws UnsupportedEncodingException {
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", getHashString(""));
		assertEquals("bde52cb31de33e46245e05fbdbd6fb24", getHashString("a"));
		assertEquals("a448017aaf21d8525fc10ae87aa6729d", getHashString("abc"));
		assertEquals("d9130a8164549fe818874806e1c7014b", getHashString("message digest"));
		assertEquals("d79e1c308aa5bbcdeea8ed63df412da9", getHashString("abcdefghijklmnopqrstuvwxyz"));
		assertEquals("043f8582f241db351ce627e153e7f0e4", getHashString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
		assertEquals("e33b4ddc9c38f2199c3e7b164fcc0536", getHashString("12345678901234567890123456789012345678901234567890123456789012345678901234567890"));
	}
	
	
}
