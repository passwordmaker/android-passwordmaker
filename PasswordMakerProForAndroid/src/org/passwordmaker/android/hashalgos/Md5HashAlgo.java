package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

public class Md5HashAlgo extends MessageDigestHashAlgo {

	public Md5HashAlgo() throws NoSuchAlgorithmException {
		super("MD5");
	}


}
