package org.passwordmaker.android.hashalgos;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningNormalHashAlgo;

import org.passwordmaker.android.hashalgos.thirdparty.Md4;

public class Md4HashAlgo extends UnderliningNormalHashAlgo {

	public Md4HashAlgo() throws NoSuchAlgorithmException {
		
	}
	
	public HashAlgo getAlgo() {
		return HashAlgo.MD5;
	}

	public int digestLength() {
		// TODO Auto-generated method stub
		return Md4.HASH_SIZE;
	}

	@Override
	protected byte[] hashText(String text) {
		try {
			Md4 digest = new Md4();
			digest.update(text.getBytes("UTF8"));
			return digest.digest();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


}
