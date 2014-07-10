package org.passwordmaker.android;

import org.daveware.passwordmaker.RDFDatabaseWriter;
import org.daveware.passwordmaker.xmlwrappers.XmlIOException;
import org.daveware.passwordmaker.xmlwrappers.XmlStreamWriter;
import org.passwordmaker.android.xmlwrappers.AndroidXmlStreamWriter;

import java.io.Writer;

/**
 * This is a specialized version of the default RDFDatabaseWriter that just changes the underlining XMLStreamWriter
 * to one that exist in the Android ecosystem.
 */
public class AndroidRDFDatabaseWriter extends RDFDatabaseWriter {
    @Override
    protected XmlStreamWriter newXmlStreamWriter(Writer writer) throws XmlIOException {
        return new AndroidXmlStreamWriter(writer);
    }
}
