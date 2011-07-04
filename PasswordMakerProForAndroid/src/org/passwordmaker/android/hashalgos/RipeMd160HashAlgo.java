package org.passwordmaker.android.hashalgos;

import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.PwmHashAlgorithm.UnderliningNormalHashAlgo;
import org.passwordmaker.android.hashalgos.thirdparty.RipeMd160;

public class RipeMd160HashAlgo  extends UnderliningNormalHashAlgo {

	public int digestLength() {
		return RipeMd160.DIGEST_SIZE;
	}

	@Override
	protected byte[] hashText(String text) {
		try {
			RipeMd160 digest = new RipeMd160();
			digest.update(text.getBytes("UTF8"));
			return digest.digest();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
