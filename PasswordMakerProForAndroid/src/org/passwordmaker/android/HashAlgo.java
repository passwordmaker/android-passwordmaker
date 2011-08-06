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

package org.passwordmaker.android;

public enum HashAlgo {
	MD4("MD4"),
	HMAC_MD4("MD4"),
	MD5("MD5"),
	MD5_Version_0_6("MD5"),
	HMAC_MD5("MD5"),
	HMAC_MD5_Version_0_6("MD5"),
	SHA_1("SHA1"),
	HMAC_SHA_1("SHA1"),
	SHA_256("SHA256"),
	HMAC_SHA_256("SHA256"),
	HMAC_SHA_256_Version_1_5_1("SHA256"),
	RIPEMD_160("RIPEMD160"),
	HMAC_RIPEMD_160("RIPEMD160");
	
	private final String digestName;
	
	private HashAlgo(String messageDigest) {
		digestName = messageDigest;
	}
	
	public String getDigestName() {
		return digestName;
	}
	
}