package org.passwordmaker.android.hashalgos;

import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.HashAlgo;

public class Sha1HashAlgo extends MessageDigestHashAlgo {

	public Sha1HashAlgo() throws NoSuchAlgorithmException {
		super(HashAlgo.SHA_1);
	}


}
