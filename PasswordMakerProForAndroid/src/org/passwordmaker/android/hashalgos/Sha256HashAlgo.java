package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

public class Sha256HashAlgo extends MessageDigestHashAlgo {

	public Sha256HashAlgo() throws NoSuchAlgorithmException {
		super("SHA-256");
	}


}
