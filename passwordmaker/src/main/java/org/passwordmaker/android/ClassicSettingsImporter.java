package org.passwordmaker.android;

import com.tasermonkeys.google.json.Gson;
import com.tasermonkeys.google.json.JsonElement;
import com.tasermonkeys.google.json.JsonObject;
import com.tasermonkeys.google.json.JsonParser;
import com.tasermonkeys.google.json.reflect.TypeToken;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.LeetLevel;
import org.daveware.passwordmaker.LeetType;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ClassicSettingsImporter {
    private Database database = new Database();
    private Gson gson = new Gson();


    public ClassicSettingsImporter(Reader reader) throws IOException {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(reader);
        JsonObject obj = element.getAsJsonObject();
        parseAccountList(database.getRootAccount(), obj);
    }

    public Database toDatabase() {
        return database;
    }

    public void parseAccountList(Account parent, JsonObject accounts) throws IOException {
        for (Map.Entry<String, JsonElement> x : accounts.entrySet()) {
            try {
                database.addAccount(parent, parseAccount((JsonObject)x.getValue()) );
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private Account parseAccount(JsonObject jsonAccount) throws IOException {
        Account account = new Account(jsonAccount.get("name").getAsString(), "", jsonAccount.get("username").getAsString());
        account.setId(UUID.randomUUID().toString());
        account.setCharacterSet(jsonAccount.get("characters").getAsString());
        OldHashAlgo hashAlgo = OldHashAlgo.valueOf(jsonAccount.get("currentAlgo").getAsString());
        account.setAlgorithm(hashAlgo.algo.getAlgo());
        account.setHmac(hashAlgo.algo.isHMac());
        account.setTrim(hashAlgo.algo.isTrimmed());
        OldLeetLevel leetLevel =OldLeetLevel.valueOf(jsonAccount.get("leetLevel").getAsString());
        account.setLeetLevel(leetLevel.leetLevel);
        OldUseLeet useLeet = OldUseLeet.valueOf(jsonAccount.get("useLeet").getAsString());
        account.setLeetType(useLeet.leetType);
        account.setModifier(jsonAccount.get("modifier").getAsString());
        account.setPrefix(jsonAccount.get("passwordPrefix").getAsString());
        account.setSuffix(jsonAccount.get("passwordSuffix").getAsString());
        Set<Account.UrlComponents> esUrls = parseUrlParts(jsonAccount.get("urlComponents"));
        account.setUrlComponents(esUrls);
        account.setLength(jsonAccount.get("lengthOfPassword").getAsShort());

        return account;
    }

    private Set<Account.UrlComponents> parseUrlParts(JsonElement urlSetElement) {
        List<String> urlCompondents = gson.fromJson( urlSetElement, new TypeToken<List<String>>() {}.getType());
        Set<Account.UrlComponents> esUrls = EnumSet.noneOf(Account.UrlComponents.class);
        for (String urlComp : urlCompondents) {
            esUrls.add(OldUrlComponents.valueOf(urlComp).urlComponent);
        }
        return esUrls;
    }

    private enum OldHashAlgo {
        MD4(AlgorithmSelectionValues.MD4),
        HMAC_MD4(AlgorithmSelectionValues.HMAC_MD4),
        MD5(AlgorithmSelectionValues.MD5),
        MD5_Version_0_6(AlgorithmSelectionValues.MD5_06),
        HMAC_MD5(AlgorithmSelectionValues.HMAC_MD5),
        HMAC_MD5_Version_0_6(AlgorithmSelectionValues.HMAC_MD5_06),
        SHA_1(AlgorithmSelectionValues.SHA1),
        HMAC_SHA_1(AlgorithmSelectionValues.HMAC_SHA1),
        SHA_256(AlgorithmSelectionValues.HMAC_SHA256),
        HMAC_SHA_256(AlgorithmSelectionValues.HMAC_SHA256),
        HMAC_SHA_256_Version_1_5_1(AlgorithmSelectionValues.HMAC_SHA256),
        RIPEMD_160(AlgorithmSelectionValues.RIPEMD160),
        HMAC_RIPEMD_160(AlgorithmSelectionValues.HMAC_RIPEMD160);

        final AlgorithmSelectionValues algo;

        OldHashAlgo(AlgorithmSelectionValues algo) {
            this.algo = algo;
        }
    }

    private enum OldLeetLevel {
        One(LeetLevel.LEVEL1),
        Two(LeetLevel.LEVEL2),
        Three(LeetLevel.LEVEL3),
        Four(LeetLevel.LEVEL4),
        Five(LeetLevel.LEVEL5),
        Six(LeetLevel.LEVEL6),
        Seven(LeetLevel.LEVEL7),
        Eight(LeetLevel.LEVEL8),
        Nine(LeetLevel.LEVEL9);

        public final LeetLevel leetLevel;

        OldLeetLevel(LeetLevel leetLevel) {
            this.leetLevel = leetLevel;
        }
    }

    private enum OldUseLeet {
        NotAtAll(LeetType.NONE),
        BeforeGeneratingPassword(LeetType.BEFORE),
        AfterGeneratingPassword(LeetType.AFTER),
        BeforeAndAfterGeneratingPassword(LeetType.BOTH);

        public final LeetType leetType;

        OldUseLeet(LeetType leetType) {
            this.leetType = leetType;
        }
    }

    private enum OldUrlComponents {
        Protocol(Account.UrlComponents.Protocol),
        Subdomain(Account.UrlComponents.Subdomain),
        Domain(Account.UrlComponents.Domain),
        PortPathAnchorQuery(Account.UrlComponents.PortPathAnchorQuery);

        public final Account.UrlComponents urlComponent;


        OldUrlComponents(Account.UrlComponents urlComponent) {
            this.urlComponent = urlComponent;
        }

    }


}
