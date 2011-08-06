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

package org.passwordmaker.android.hashalgos;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningNormalHashAlgo;

public class MessageDigestHashAlgo extends UnderliningNormalHashAlgo {

	private final int _digestLength;
	private final String digestName;
	private final HashAlgo hashAlgo;
	
	public MessageDigestHashAlgo(HashAlgo hashAlgo) throws NoSuchAlgorithmException {
		this.digestName = hashAlgo.getDigestName();
		this.hashAlgo = hashAlgo;
		final MessageDigest digest = MessageDigest.getInstance(digestName);
		_digestLength = digest.getDigestLength();
		
	}
	
	public HashAlgo getAlgo() {
		return hashAlgo;
	}
	
	public int digestLength() {
		return _digestLength;
	}

	@Override
	protected byte[] hashText(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance(digestName);
			digest.update(text.getBytes("UTF8"));
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
