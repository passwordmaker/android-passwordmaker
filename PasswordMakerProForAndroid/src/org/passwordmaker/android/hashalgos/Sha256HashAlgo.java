package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.HashAlgo;

public class Sha256HashAlgo extends MessageDigestHashAlgo {

	public Sha256HashAlgo() throws NoSuchAlgorithmException {
		super(HashAlgo.SHA_256);
	}


}
