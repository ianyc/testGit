package com.chtn.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.sun.xml.parser.Resolver;
import com.sun.xml.tree.XmlDocument;
import org.xml.sax.*;

public class XMLUtil {
    public static XmlDocument parseXml(byte[] rawdata)  throws IOException{
      try {
        ByteArrayInputStream in=new ByteArrayInputStream(rawdata);
        InputSource input = Resolver.createInputSource( "text/xml;charset=Big5", in, false, "US-ASCII");
        return XmlDocument.createXmlDocument(input, false);
      }catch (SAXParseException e) {
        e.printStackTrace();
        throw new IOException("XML文件解析錯誤,文件位置：line " + e.getLineNumber () +"，uri "+ e.getSystemId ());
      }catch (SAXException e) {
          Exception x = e.getException ();
          ((x == null) ? e : x).printStackTrace ();
          return null;
      }catch (Throwable t) {
          t.printStackTrace ();
          return null;
      }
  }
}
