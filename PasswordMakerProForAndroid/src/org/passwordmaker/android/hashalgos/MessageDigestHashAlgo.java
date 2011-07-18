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
