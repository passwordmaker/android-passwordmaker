package org.passwordmaker.android;

public enum HashAlgo {
	MD4("MD4"),
	HMAC_MD4("MD4"),
	MD5("MD5"),
	MD5_Version_0_6("MD5"),
	HMAC_MD5("MD5"),
	HMAC_MD5_Version_0_6("MD5"),
	SHA_1("SHA1"),
	HMAC_SHA_1("SHA1"),
	SHA_256("SHA256"),
	HMAC_SHA_256("SHA256"),
	HMAC_SHA_256_Version_1_5_1("SHA256"),
	RIPEMD_160("RIPEMD160"),
	HMAC_RIPEMD_160("RIPEMD160");
	
	private final String digestName;
	
	private HashAlgo(String messageDigest) {
		digestName = messageDigest;
	}
	
	public String getDigestName() {
		return digestName;
	}
	
}