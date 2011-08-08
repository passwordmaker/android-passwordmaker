/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android.test;
import junit.framework.TestCase;
import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.hashalgos.thirdparty.RipeMd160;

public class PasswordMakerRipeMd160Test extends TestCase {
	public static String toHexString(byte[]bytes) {
	    StringBuilder sb = new StringBuilder(bytes.length*2);
	    for(byte b: bytes)
	      sb.append(Integer.toHexString(b+0x800).substring(1));
	    return sb.toString();
	}
	
	private String getHashString(String text) throws UnsupportedEncodingException {
		RipeMd160 digest = new RipeMd160();
		digest.update(text.getBytes("UTF-8"));
		byte[] result = digest.digest();
		String resultStr = toHexString(result);
		return resultStr;
	}
	
	/**
	 * From http://en.wikipedia.org/wiki/RIPEMD#RIPEMD-160_hashes
	 * RIPEMD-160("The quick brown fox jumps over the lazy dog") = 37f332f68db77bd9d7edd4969571ad671cf9dd3b
 	 * RIPEMD-160("The quick brown fox jumps over the lazy cog") = 132072df690933835eb8b6ad0b77e7b6f14acad7
     * 
	 * From Passwordmaker-iphone unit tests:
	 * RIPEMD-160("Hello World") = a830d7beb04eb7549ce990fb7dc962e499a27230
	 * @throws UnsupportedEncodingException
	 */
	public void testHash() throws UnsupportedEncodingException {
		assertEquals("9c1185a5c5e9fc54612808977ee8f548b2258d31", getHashString(""));
		assertEquals("37f332f68db77bd9d7edd4969571ad671cf9dd3b", getHashString("The quick brown fox jumps over the lazy dog"));
		assertEquals("132072df690933835eb8b6ad0b77e7b6f14acad7",  getHashString("The quick brown fox jumps over the lazy cog"));
		assertEquals("a830d7beb04eb7549ce990fb7dc962e499a27230", getHashString("Hello World"));
	}
}
