package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

public class Sha1HashAlgo extends MessageDigestHashAlgo {

	public Sha1HashAlgo() throws NoSuchAlgorithmException {
		super("SHA-1");
	}


}
