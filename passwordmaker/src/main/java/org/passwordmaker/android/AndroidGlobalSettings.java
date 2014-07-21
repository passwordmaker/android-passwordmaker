package org.passwordmaker.android;

import org.daveware.passwordmaker.GlobalSettingKey;

public class AndroidGlobalSettings {
    public final static GlobalSettingKey FAVORITES = new GlobalSettingKey("NS1:favorites", "");
    public final static GlobalSettingKey MASTER_PASSWORD_HASH = new GlobalSettingKey("NS1:MASTER_PWD_HASH", "");
    public final static GlobalSettingKey STORE_MASTER_PASSWORD_HASH = new GlobalSettingKey("NS1:STORE_MASTER_PWD_HASH", "false");
    public final static GlobalSettingKey MASTER_PASSWORD_SALT = new GlobalSettingKey("NS1:MASTER_PWD_SALT", "");

}
