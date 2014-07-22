package org.passwordmaker.android;

import android.content.Context;
import android.util.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tasermonkeys.google.json.*;
import com.tasermonkeys.google.json.reflect.TypeToken;
import org.daveware.passwordmaker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

import static com.google.common.io.Closeables.closeQuietly;

public class ClassicSettingsImporter {
    private final  Database database = new Database();
    private final Set<String> favorites = Sets.newHashSet();
    private final Gson gson = new Gson();
    private static final String REPO_PROFILES_FILENAME = "profiles.pss";
    private static final String UPGRADED_MARKER = "profiles.upgrademarker";
    private static final String LOG_TAG = "PWM/CSI";


    private ClassicSettingsImporter(Reader reader) throws IOException {
        readFromReader(reader);
    }

    private void readFromReader(Reader reader) throws IOException {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(reader);
        JsonObject obj = element.getAsJsonObject();
        parseAccountList(database.getRootAccount(), obj);
    }

    protected Database toDatabase() {
        return database;
    }

    protected Collection<String> getFavorites() {
        return favorites;
    }

    /**
     * This function will only upgrade once.  After the first time ran, its a no-op.
     * Side effect is a new file on the filesystem will be created named {@value #UPGRADED_MARKER}
     * @param context      - The context to which base the filesystem calls on
     * @param favoritesOut - it clears it out, then add all favorites from all of the extracted profiles
     *
     * @return a created database if successful.  Null otherwise.
     * @throws IOException - On error importing the database.
     */
    @Nullable
    public static Database importIntoDatabase(@NotNull Context context, @NotNull Collection<String> favoritesOut) throws IOException {
        File f = new File(context.getFilesDir(), REPO_PROFILES_FILENAME);
        File upgradedMarker = new File(context.getFilesDir(), UPGRADED_MARKER);
        // The upgrade marker is so that we can only upgrade once, but at the same time allow for the possibility of a
        // downgrade of software version if a bug happens.
        if (!f.exists() || upgradedMarker.exists() )
            return null;
        Log.i(LOG_TAG, "Upgrading classic database to new one");
        InputStream fis = context.openFileInput(REPO_PROFILES_FILENAME);
        Database db = null;
        try {
            Reader reader = new InputStreamReader(fis, "UTF-8");
            ClassicSettingsImporter importer = new ClassicSettingsImporter(reader);
            db = importer.toDatabase();
            favoritesOut.clear();
            favoritesOut.addAll(importer.getFavorites());
        } finally {
            closeQuietly(fis);
        }
        // only will get here if we didn't throw before
        FileOutputStream touchFile = null;
        try {
            touchFile = new FileOutputStream(upgradedMarker);
            touchFile.write(new Date().toString().getBytes());
        } catch (IOException ignored) {
        } finally {
            try {
                if ( touchFile != null ) touchFile.close();
            } catch (IOException ignore) {}
        }
        return db;
    }

    protected void parseAccountList(Account parent, JsonObject accounts) throws IOException {
        for (Map.Entry<String, JsonElement> x : accounts.entrySet()) {
            try {
                database.addAccount(parent, parseAccount((JsonObject)x.getValue()) );
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private Account parseAccount(JsonObject jsonAccount) {
        Account account = new Account(jsonAccount.get("name").getAsString(), "", jsonAccount.get("username").getAsString());
        account.setId(Account.createId());
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
        List<String> accountFavs = asStringArray(jsonAccount.getAsJsonArray("pwmFavoriteInputs"));
        // this will allow these accounts to be auto-selected for the favorites
        for (String fav : accountFavs ) {
            AccountPatternData accPtnData = new AccountPatternData();
            accPtnData.setDesc(fav + ": Imported from classic");
            accPtnData.setEnabled(true);
            accPtnData.setPattern(fav);
            accPtnData.setType(AccountPatternType.WILDCARD);
            account.getPatterns().add(accPtnData);
        }
        favorites.addAll(accountFavs);

        return account;
    }

    private List<String> asStringArray(JsonArray array) {
        List<String> errs = Lists.newArrayList();
        List<String> result = Lists.newArrayListWithCapacity(array.size());
        for (int i = 0; i < array.size(); ++i) {
            try {
                result.add(array.get(i).getAsString());
            } catch (Exception e) {
                errs.add(e.getMessage());
            }
        }
        if ( ! errs.isEmpty() ) {
            Log.e(LOG_TAG, "Error while reading json array: " + errs.toString(), new IOException(errs.get(0)));
        }
        return result;
    }

    private Set<Account.UrlComponents> parseUrlParts(JsonElement urlSetElement) {
        List<String> urlComponents = gson.fromJson( urlSetElement, new TypeToken<List<String>>() {}.getType());
        Set<Account.UrlComponents> esUrls = EnumSet.noneOf(Account.UrlComponents.class);
        for (String urlComp : urlComponents) {
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
