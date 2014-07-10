package org.passwordmaker.android;

import org.daveware.passwordmaker.GlobalSettingKey;

public class AndroidGlobalSettings {
    public static GlobalSettingKey FAVORITES = new GlobalSettingKey("NS1:favorites", "");
    public static GlobalSettingKey MASTER_PASSWORD_HASH = new GlobalSettingKey("NS1:MASTER_PWD_HASH", "");
    public static GlobalSettingKey STORE_MASTER_PASSWORD_HASH = new GlobalSettingKey("NS1:STORE_MASTER_PWD_HASH", "false");
    public static GlobalSettingKey MASTER_PASSWORD_SALT = new GlobalSettingKey("NS1:MASTER_PWD_SALT", "");

}
