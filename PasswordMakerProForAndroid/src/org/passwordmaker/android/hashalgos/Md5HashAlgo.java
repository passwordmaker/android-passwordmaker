package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.HashAlgo;

public class Md5HashAlgo extends MessageDigestHashAlgo {

	public Md5HashAlgo() throws NoSuchAlgorithmException {
		super(HashAlgo.MD5);
	}


}
