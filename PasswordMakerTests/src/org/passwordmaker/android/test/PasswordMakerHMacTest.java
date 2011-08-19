package org.passwordmaker.android.test;

import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningHashAlgo;

import junit.framework.TestCase;

public class PasswordMakerHMacTest  extends TestCase {
	public static String toHexString(byte[]bytes) {
	    StringBuilder sb = new StringBuilder(bytes.length*2);
	    for(byte b: bytes)
	      sb.append(Integer.toHexString(b+0x800).substring(1));
	    return sb.toString();
	}

	
	private String getHashString(HashAlgo hashAlgo, String key, String text) throws UnsupportedEncodingException {
		UnderliningHashAlgo hasher = PwmHashAlgorithm.getUnderliningHasher(hashAlgo);
		return toHexString( hasher.getHashBlob(key, text) );
	}
	
	/**
	 * HMAC_MD5("", "") = 0x 74e6f7298a9c2d168935f58c001bad88
HMAC_SHA1("", "") = 0x fbdb1d1b18aa6c08324b7d64b71fb76370690e1d
HMAC_SHA256("", "") = 0x b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad 
HMAC_SHA512("", "") = 0x b936cee86c9f87aa5d3c6f2e84cb5a4239a5fe50480a6ec66b70ab5b1f4ac6730c6c515421b327ec1d69402e53dfb49ad7381eb067b338fd7b0cb22247225d47
Here are some non-empty HMAC values -
HMAC_MD5("key", "The quick brown fox jumps over the lazy dog") = 0x 80070713463e7749b90c2dc24911e275
HMAC_SHA1("key", "The quick brown fox jumps over the lazy dog") = 0x de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9
HMAC_SHA256("key", "The quick brown fox jumps over the lazy dog") = 0x f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8
HMAC_SHA512("key", "The quick brown fox jumps over the lazy dog") = 0x b42af09057bac1e2d41708e48a902e09b5ff7f12ab428a4fe86653c73dd248fb82f948a549f7b791a5b41915ee4d1ec3935357e4e2317250d0372afa2ebeeb3a 
	 */
	public static final String sampleKey = "key";
	public static final String sampleText = "The quick brown fox jumps over the lazy dog";
	
	public void testEmptyMd5() throws UnsupportedEncodingException {
		assertEquals( "74e6f7298a9c2d168935f58c001bad88", getHashString(HashAlgo.HMAC_MD5, "",  ""));
	}
	
	public void testEmptySha1() throws UnsupportedEncodingException{
		assertEquals( "fbdb1d1b18aa6c08324b7d64b71fb76370690e1d", getHashString(HashAlgo.HMAC_SHA_1, "",  ""));
	}

	public void testEmptySha256() throws UnsupportedEncodingException{
		assertEquals( "b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad", getHashString(HashAlgo.HMAC_SHA_256, "",  ""));
	}
	
	public void testTextMd5() throws UnsupportedEncodingException{
		assertEquals( "80070713463e7749b90c2dc24911e275", getHashString(HashAlgo.HMAC_MD5, sampleKey, sampleText));
	}
	
	public void testTextSha1() throws UnsupportedEncodingException {
		assertEquals( "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9", getHashString(HashAlgo.HMAC_SHA_1, sampleKey, sampleText));
	}

	public void testTextSha256() throws UnsupportedEncodingException {
		assertEquals( "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8", getHashString(HashAlgo.HMAC_SHA_256, sampleKey, sampleText));
	}
	
}
