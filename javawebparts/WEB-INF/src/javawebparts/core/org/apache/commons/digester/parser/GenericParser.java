/* $Id: GenericParser.java 133 2005-09-27 04:07:30Z fzammetti $
 *
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package javawebparts.core.org.apache.commons.digester.parser;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

/**
 * Create a <code>SAXParser</code> configured to support XML Schema and DTD.
 *
 * @since 1.6
 */

public class GenericParser{

    /**
     * The Log to which all SAX event related logging calls will be made.
     */
    protected static Log log =
        LogFactory.getLog("org.apache.commons.digester.Digester.sax");

    /**
     * The JAXP 1.2 property required to set up the schema location.
     */
    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * The JAXP 1.2 property to set up the schemaLanguage used.
     */
    protected static String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /**
     * Create a <code>SAXParser</code> configured to support XML Scheman and DTD
     * @param properties parser specific properties/features
     * @return an XML Schema/DTD enabled <code>SAXParser</code>
     */
    public static SAXParser newSAXParser(Properties properties)
            throws ParserConfigurationException,
                   SAXException,
                   SAXNotRecognizedException{

        SAXParserFactory factory =
                        (SAXParserFactory)properties.get("SAXParserFactory");
        SAXParser parser = factory.newSAXParser();
        String schemaLocation = (String)properties.get("schemaLocation");
        String schemaLanguage = (String)properties.get("schemaLanguage");

        try{
            if (schemaLocation != null) {
                parser.setProperty(JAXP_SCHEMA_LANGUAGE, schemaLanguage);
                parser.setProperty(JAXP_SCHEMA_SOURCE, schemaLocation);
            }
        } catch (SAXNotRecognizedException e){
            log.info(parser.getClass().getName() + ": "
                                        + e.getMessage() + " not supported.");
        }
        return parser;
    }

}