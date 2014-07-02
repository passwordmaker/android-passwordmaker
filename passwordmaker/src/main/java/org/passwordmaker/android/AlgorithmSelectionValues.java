package org.passwordmaker.android;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AlgorithmType;

import java.util.NoSuchElementException;

@SuppressWarnings("UnusedDeclaration")
public enum AlgorithmSelectionValues {
    // The stringResourcePosition must match that of the strings.xml HashAlgos array index

    MD4(AlgorithmType.MD4, false, true, 0),
    HMAC_MD4(AlgorithmType.MD4, true, true, 1),
    MD5(AlgorithmType.MD5, false, true, 2),
    MD5_06(AlgorithmType.MD5, false, false, 3),
    HMAC_MD5(AlgorithmType.MD5, true, true, 4),
    HMAC_MD5_06(AlgorithmType.MD5, true, false, 5),
    SHA1(AlgorithmType.SHA1, false, true, 6),
    HMAC_SHA1(AlgorithmType.SHA1, true, true, 7),
    SHA256(AlgorithmType.SHA256, false, true, 8),
    HMAC_SHA256(AlgorithmType.SHA256, true, true, 9),
    RIPEMD160(AlgorithmType.RIPEMD160, false, true, 10),
    HMAC_RIPEMD160(AlgorithmType.RIPEMD160, true, true, 11)
    ;


    private final AlgorithmType aglo;
    private final boolean isHMac;
    private final boolean isTrimmed;
    private final int stringResourcePosition;

    AlgorithmSelectionValues(AlgorithmType aglo, boolean isHMac, boolean isTrimmed, int stringResourcePosition) {
        this.aglo = aglo;
        this.isHMac = isHMac;
        this.isTrimmed = isTrimmed;
        this.stringResourcePosition = stringResourcePosition;
    }

    public AlgorithmType getAlgo() {
        return aglo;
    }

    public boolean isHMac() {
        return isHMac;
    }

    public boolean isTrimmed() {
        return isTrimmed;
    }

    public int getStringResourcePosition() {
        return stringResourcePosition;
    }

    public static AlgorithmSelectionValues getByPosition(int index) {
        for (AlgorithmSelectionValues v : values()) {
            if ( v.stringResourcePosition == index ) return v;
        }
        throw new NoSuchElementException("Invalid index: " + index);
    }

    public static AlgorithmSelectionValues getFromAccount(Account account) {
        for (AlgorithmSelectionValues v : values()) {
            if ( v.isHMac == account.isHmac() &&
                 v.isTrimmed == account.isTrim() &&
                 v.getAlgo() == account.getAlgorithm() ) return v;
        }
        throw new NoSuchElementException("Could not find algorithm selection combination from account '"
           + account.getId() + "(" + account.getName() + ")' setup");
    }

    public void setAccountSettings(Account account) {
        account.setHmac(isHMac);
        account.setTrim(isTrimmed);
        account.setAlgorithm(aglo);
    }
}
