package org.passwordmaker.android.xmlwrappers;

import org.daveware.passwordmaker.xmlwrappers.XmlIOException;
import org.daveware.passwordmaker.xmlwrappers.XmlStreamWriter;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class AndroidXmlStreamWriter implements XmlStreamWriter {
    XmlPullParserFactory factory = null;
    XmlSerializer serializer;
    Writer writer;

    private static class Tag {
        final String name;
        final String namespace;

        public Tag(String name, String namespace) {
            this.name = name;
            this.namespace = namespace;
        }
    }

    protected Tag rootTag = null;

    protected LinkedList<Tag> tagStack = new LinkedList<Tag>();

    protected Tag addTagToStack(String name, String namespace) throws IOException {
        Tag ret = new Tag(name, namespace);
        // Stupid XmlSerilizer from xmlpull does this in the different order as the javax.xml.stream does.
        // For namespace declarations, they must happen before the tag is added to the serializer.
        if ( tagStack.isEmpty() ) {
            if ( rootTag == null ) {
                rootTag = ret;
                return rootTag;
            } else {
                serializer.startTag(rootTag.namespace, rootTag.name);
                tagStack.push(rootTag);
            }
        }
        tagStack.push(ret);
        serializer.startTag(ret.namespace, ret.name);
        return ret;
    }

    protected void flushRootIfNeeded() throws IOException {
        if ( tagStack.isEmpty() && rootTag != null ) {
            serializer.startTag(rootTag.namespace, rootTag.name);
            tagStack.push(rootTag);
        }
    }

    protected Tag peekTagStack() {
        return tagStack.peek();
    }

    protected Tag popTagStack() {
        return tagStack.pop();
    }

    public AndroidXmlStreamWriter(Writer writer) throws XmlIOException {
        try {
            this.writer = writer;
            factory = XmlPullParserFactory.newInstance(
                    System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            if ( factory == null ) throw new XmlIOException("Can not create an XmlPullFactory");
            serializer = factory.newSerializer();
            serializer.setOutput(writer);
        } catch (XmlPullParserException e) {
            throw new XmlIOException(e);
        } catch (IOException e) {
            throw new XmlIOException(e);
        }

    }

    @Override
    public void writeStartDocument() throws XmlIOException {
        try {
            serializer.startDocument("UTF-8", null);
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XmlIOException {
        try {
            serializer.startDocument(encoding, null);
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void addPrefix(String prefix, String namespace) throws XmlIOException {
        try {
            serializer.setPrefix(prefix, namespace);
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }


    @Override
    public void writeStartElement(String name) throws XmlIOException {
        try {
            addTagToStack(name, "");
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void writeAttribute(String localname, String value) throws XmlIOException {
        try {
            // alright, if we are asking to write to an attribute we must be done with writing any namespaces.
            flushRootIfNeeded();
            serializer.attribute("", localname, value);
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void writeEndElement() throws XmlIOException {
        try {
            // if we ask to end our element, and we haven't done anything with the root, let flush that first
            flushRootIfNeeded();
            Tag lastTag = peekTagStack();
            serializer.endTag(lastTag.namespace, lastTag.name);
            popTagStack(); // only pop if no exception was thrown, this way we can debug.
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void writeEndDocument() throws XmlIOException {
        try {
            serializer.endDocument();
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void flush() throws XmlIOException {
        try {
            serializer.flush();
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }

    @Override
    public void close() throws XmlIOException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new XmlIOException(e);
        }
    }
}
