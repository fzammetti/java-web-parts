/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


package javawebparts.core.org.apache.commons.beanutils.converters;


import java.math.BigInteger;
import javawebparts.core.org.apache.commons.beanutils.ConversionException;
import javawebparts.core.org.apache.commons.beanutils.Converter;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a <code>java.math.BigInteger</code> object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion
 * error occurs.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 134 $ $Date: 2005-09-27 04:56:51 +0000 (Tue, 27 Sep 2005) $
 * @since 1.3
 */

public final class BigIntegerConverter implements Converter {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     */
    public BigIntegerConverter() {

        this.defaultValue = null;
        this.useDefault = false;

    }


    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue The default value to be returned
     */
    public BigIntegerConverter(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The default value specified to our Constructor, if any.
     */
    private Object defaultValue = null;


    /**
     * Should we return the default value on conversion errors?
     */
    private boolean useDefault = true;


    // --------------------------------------------------------- Public Methods


    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value) {

        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        if (value instanceof BigInteger) {
            return (value);
        }

        try {
            return (new BigInteger(value.toString()));
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }

    }


}
