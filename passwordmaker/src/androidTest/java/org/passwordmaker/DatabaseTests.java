package org.passwordmaker;

import junit.framework.TestCase;
import org.daveware.passwordmaker.*;
import org.passwordmaker.android.AndroidRDFDatabaseWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DatabaseTests extends TestCase {
    public Account makeADefaultAccount() {
        Account account = Account.makeDefaultAccount();
        account.setAlgorithm(AlgorithmType.MD5);
        account.setCharacterSet(CharacterSets.ALPHANUMERIC);
        return account;
    }

    public Account makeAccount() throws Exception {
        return new AccountBuilder().setLength(12)
                .setAlgorithm(AlgorithmType.SHA256)
                .setDesc("Test account")
                .setName("TestAccount")
                .setId("rdf:#$da39a3ee5e6b4b0d3255bfef95601890afd80709")
                .addWildcardPattern("allGoogle", "http*://*google.com/*").build();
    }

    public void testWriteDatabaseOutToRdf() throws Exception {
        RDFDatabaseWriter writer = new AndroidRDFDatabaseWriter();
        Database database = new Database();
        database.addAccount(database.getRootAccount(), makeADefaultAccount());
        database.addAccount(database.getRootAccount(), makeAccount());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.write(os, database);
    }



    public void testWriteAccount() throws Exception {
        final String expectedXml = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<RDF:Description RDF:about=\"rdf:#$[B@5a676437\" NS1:name=\"Yahoo.com\" " +
                "NS1:description=\"Im a description\" NS1:whereLeetLB=\"off\" NS1:leetLevelLB=\"1\" " +
                "NS1:hashAlgorithmLB=\"hmac-rmd160\" NS1:passwordLength=\"15\" NS1:usernameTB=\"myusername\" NS1:counter=\"Another Modifier\" " +
                "NS1:charset='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&amp;*()_-+={}|[]\\:\";&apos;&lt;&gt;?,./' " +
                "NS1:prefix=\"\" NS1:suffix=\"\" NS1:autoPopulate=\"false\" NS1:urlToUse=\"yahoo.com\" />";


        Account acc = new Account("Yahoo.com", "Im a description", "yahoo.com", "myusername",
                AlgorithmType.RIPEMD160, true, true, 15, CharacterSets.BASE_93_SET,
                LeetType.NONE, LeetLevel.LEVEL1, "Another Modifier", "", "", false, "rdf:#$[B@5a676437");
        RDFDatabaseWriter writer = new AndroidRDFDatabaseWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        writer.serializeAccount(os, acc);
        String serializedString = os.toString("UTF-8");
        assertEquals("Unexpected RDF came out of the RDFDatabaseWriter", expectedXml, serializedString);
    }

    public void testReadingAccount() throws Exception {
        final String inputXml = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<RDF:Description RDF:about=\"rdf:#$[B@5a676437\" NS1:name=\"Yahoo.com\" " +
                "NS1:description=\"Im a description\" NS1:whereLeetLB=\"off\" NS1:leetLevelLB=\"1\" " +
                "NS1:hashAlgorithmLB=\"hmac-rmd160\" NS1:passwordLength=\"15\" NS1:usernameTB=\"myusername\" NS1:counter=\"Another Modifier\" " +
                "NS1:charset='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&amp;*()_-+={}|[]\\:\";&apos;&lt;&gt;?,./' " +
                "NS1:prefix=\"\" NS1:suffix=\"\" NS1:autoPopulate=\"false\" NS1:urlToUse=\"yahoo.com\" />";
        RDFDatabaseReader reader = new RDFDatabaseReader();
        Account actual = reader.deserializeAccount(new ByteArrayInputStream(inputXml.getBytes()));

        assertEquals("Yahoo.com", actual.getName());
        assertEquals("yahoo.com", actual.getUrl());
        assertEquals(CharacterSets.BASE_93_SET, actual.getCharacterSet());
        assertEquals(15, actual.getLength());
        assertEquals(AlgorithmType.RIPEMD160, actual.getAlgorithm());
        assertEquals(true, actual.isHmac());
        assertEquals("Another Modifier", actual.getModifier());
        assertEquals("Im a description", actual.getDesc());
    }

    public void testReadingDatabase() throws Exception {
        final String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RDF:RDF xmlns:NS1=\"http://passwordmaker.mozdev.org/rdf#\" xmlns:NC=\"http://home.netscape.com/NC-rdf#\" xmlns:RDF=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                "    <RDF:Seq RDF:about=\"http://passwordmaker.mozdev.org/accounts\">\n" +
                "    <RDF:li RDF:resource=\"http://passwordmaker.mozdev.org/defaults\"/>\n" +
                "    <RDF:li RDF:resource=\"rdf:#$da39a3ee5e6b4b0d3255bfef95601890afd80709\"/>\n" +
                "    </RDF:Seq>\n" +
                "    <RDF:Description RDF:about=\"http://passwordmaker.mozdev.org/accounts\" NS1:name=\"default\" NS1:description=\"\" NS1:whereLeetLB=\"off\" NS1:leetLevelLB=\"1\" NS1:hashAlgorithmLB=\"md5\" NS1:passwordLength=\"8\" NS1:usernameTB=\"username\" NS1:counter=\"\" NS1:charset=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&amp;*()_-+={}|[]\\:&quot;;'&lt;&gt;?,./\" NS1:prefix=\"\" NS1:suffix=\"\" NS1:autoPopulate=\"false\" NS1:urlToUse=\"http://domain.com\"/>\n" +
                "    <RDF:Description RDF:about=\"http://passwordmaker.mozdev.org/defaults\" NS1:name=\"default\" NS1:description=\"\" NS1:whereLeetLB=\"off\" NS1:leetLevelLB=\"1\" NS1:hashAlgorithmLB=\"md5\" NS1:passwordLength=\"8\" NS1:usernameTB=\"\" NS1:counter=\"\" NS1:charset=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\" NS1:prefix=\"\" NS1:suffix=\"\" NS1:autoPopulate=\"false\" NS1:protocolCB=\"false\" NS1:subdomainCB=\"false\" NS1:domainCB=\"true\" NS1:pathCB=\"false\"/>\n" +
                "    <RDF:Description RDF:about=\"rdf:#$da39a3ee5e6b4b0d3255bfef95601890afd80709\" NS1:name=\"TestAccount\" NS1:description=\"Test account\" NS1:whereLeetLB=\"off\" NS1:leetLevelLB=\"1\" NS1:hashAlgorithmLB=\"sha256\" NS1:passwordLength=\"12\" NS1:usernameTB=\"\" NS1:counter=\"\" NS1:charset=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\" NS1:prefix=\"\" NS1:suffix=\"\" NS1:autoPopulate=\"false\" NS1:urlToUse=\"\" NS1:pattern0=\"http*://*google.com/*\" NS1:patterntype0=\"wildcard\" NS1:patternenabled0=\"true\" NS1:patterndesc0=\"allGoogle\"/>\n" +
                "    <RDF:Description RDF:about=\"http://passwordmaker.mozdev.org/globalSettings\"/>\n" +
                "    </RDF:RDF>";
        RDFDatabaseReader reader = new RDFDatabaseReader();
        Database actual = reader.read(new ByteArrayInputStream(inputXml.getBytes()));
        assertNotNull(actual.getRootAccount());
        assertTrue(actual.getRootAccount().isRoot());
        assertEquals(2, actual.getRootAccount().getChildren().size());
        Account actualDefaultAccount =  actual.findAccountById(Account.DEFAULT_ACCOUNT_URI);
        assertNotNull(actualDefaultAccount);
        assertEquals(AlgorithmType.MD5, actualDefaultAccount.getAlgorithm());
        assertEquals(CharacterSets.ALPHANUMERIC, actualDefaultAccount.getCharacterSet());
        assertEquals(8, actualDefaultAccount.getLength());
        assertTrue(actualDefaultAccount.getUrlComponents().contains(Account.UrlComponents.Domain));
        assertTrue(actualDefaultAccount.isDefault());
        Account actualAccount = actual.findAccountById("rdf:#$da39a3ee5e6b4b0d3255bfef95601890afd80709");
        assertEquals("TestAccount", actualAccount.getName());
        assertEquals("Test account", actualAccount.getDesc());
        assertEquals(1, actualAccount.getPatterns().size());
        AccountPatternData actualPattern = actualAccount.getPatterns().get(0);
        assertEquals("allGoogle", actualPattern.getDesc());
        assertEquals(AccountPatternType.WILDCARD, actualPattern.getType());
        assertEquals("http*://*google.com/*", actualPattern.getPattern());
    }

}
