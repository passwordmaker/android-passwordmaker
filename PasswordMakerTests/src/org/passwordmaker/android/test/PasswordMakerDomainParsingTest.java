package org.passwordmaker.android.test;

import junit.framework.TestCase;

import static org.passwordmaker.android.PasswordMaker.Domain;

public class PasswordMakerDomainParsingTest extends TestCase {
    public void testSimple() {
        Domain domain = new Domain("google.com");
        assertEquals("com", domain.getTld());
        assertEquals("google.com", domain.getDomain());
        assertEquals("", domain.getSubdomains());
        assertEquals("google.com", domain.fullDomain());
        domain = new Domain("google.us");
        assertEquals("us", domain.getTld());
        assertEquals("google.us", domain.getDomain());
        assertEquals("", domain.getSubdomains());
        assertEquals("google.us", domain.fullDomain());
        domain = new Domain("google.doesnotexist");
        assertEquals("doesnotexist", domain.getTld());
        assertEquals("google.doesnotexist", domain.getDomain());
        assertEquals("", domain.getSubdomains());
        assertEquals("google.doesnotexist", domain.fullDomain());
    }

    public void testMultiLevelTLD() {
        Domain domain = new Domain("google.co.uk");
        assertEquals("co.uk", domain.getTld());
        assertEquals("google.co.uk", domain.getDomain());
        assertEquals("", domain.getSubdomains());
        domain = new Domain("google.chiyoda.tokyo.jp");
        assertEquals("chiyoda.tokyo.jp", domain.getTld());
        assertEquals("google.chiyoda.tokyo.jp", domain.getDomain());
        assertEquals("", domain.getSubdomains());
        domain = new Domain("google.sa.edu.au");
        assertEquals("sa.edu.au", domain.getTld());
        assertEquals("google.sa.edu.au", domain.getDomain());
        assertEquals("", domain.getSubdomains());
    }

    public void testSimpleWithSubDomains() {
        Domain domain = new Domain("www.google.com");
        assertEquals("com", domain.getTld());
        assertEquals("google.com", domain.getDomain());
        assertEquals("www", domain.getSubdomains());
        assertEquals("www.google.com", domain.fullDomain());
        domain = new Domain("www.sci.google.us");
        assertEquals("us", domain.getTld());
        assertEquals("google.us", domain.getDomain());
        assertEquals("www.sci", domain.getSubdomains());
        assertEquals("www.sci.google.us", domain.fullDomain());
        domain = new Domain("x1.x2.google.doesnotexist");
        assertEquals("doesnotexist", domain.getTld());
        assertEquals("google.doesnotexist", domain.getDomain());
        assertEquals("x1.x2", domain.getSubdomains());
        assertEquals("x1.x2.google.doesnotexist", domain.fullDomain());
    }

    public void testMultiLevelTLDWithSubDomains() {
        Domain domain = new Domain("www.google.co.uk");
        assertEquals("co.uk", domain.getTld());
        assertEquals("google.co.uk", domain.getDomain());
        assertEquals("www", domain.getSubdomains());
        domain = new Domain("www.cs.google.co.uk");
        assertEquals("co.uk", domain.getTld());
        assertEquals("google.co.uk", domain.getDomain());
        assertEquals("www.cs", domain.getSubdomains());
        domain = new Domain("www.sci.google.chiyoda.tokyo.jp");
        assertEquals("chiyoda.tokyo.jp", domain.getTld());
        assertEquals("google.chiyoda.tokyo.jp", domain.getDomain());
        assertEquals("www.sci", domain.getSubdomains());
        domain = new Domain("x1.x2.google.sa.edu.au");
        assertEquals("sa.edu.au", domain.getTld());
        assertEquals("google.sa.edu.au", domain.getDomain());
        assertEquals("x1.x2", domain.getSubdomains());
    }

    public void testEmptyStringParsing() {
        Domain domain = new Domain("");
        assertEquals("", domain.getTld());
        assertEquals("", domain.getDomain());
        assertEquals("", domain.getSubdomains());
    }
}
