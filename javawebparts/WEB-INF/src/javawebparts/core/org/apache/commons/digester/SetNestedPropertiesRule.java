/* $Id: SetNestedPropertiesRule.java 134 2005-09-27 04:56:51Z fzammetti $
 *
 * Copyright 2003-2004 The Apache Software Foundation.
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


package javawebparts.core.org.apache.commons.digester;


import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.beans.PropertyDescriptor;

import javawebparts.core.org.apache.commons.beanutils.BeanUtils;
import javawebparts.core.org.apache.commons.beanutils.DynaBean;
import javawebparts.core.org.apache.commons.beanutils.DynaProperty;
import javawebparts.core.org.apache.commons.beanutils.PropertyUtils;

import org.xml.sax.Attributes;

import org.apache.commons.logging.Log;


/**
 * <p>Rule implementation that sets properties on the object at the top of the
 * stack, based on child elements with names matching properties on that
 * object.</p>
 *
 * <p>Example input that can be processed by this rule:</p>
 * <pre>
 *   [widget]
 *    [height]7[/height]
 *    [width]8[/width]
 *    [label]Hello, world[/label]
 *   [/widget]
 * </pre>
 *
 * <p>For each child element of [widget], a corresponding setter method is
 * located on the object on the top of the digester stack, the body text of
 * the child element is converted to the type specified for the (sole)
 * parameter to the setter method, then the setter method is invoked.</p>
 *
 * <p>This rule supports custom mapping of xml element names to property names.
 * The default mapping for particular elements can be overridden by using
 * {@link #SetNestedPropertiesRule(String[] elementNames,
 *                                 String[] propertyNames)}.
 * This allows child elements to be mapped to properties with different names.
 * Certain elements can also be marked to be ignored.</p>
 *
 * <p>A very similar effect can be achieved using a combination of the
 * <code>BeanPropertySetterRule</code> and the <code>ExtendedBaseRules</code>
 * rules manager; this <code>Rule</code>, however, works fine with the default
 * <code>RulesBase</code> rules manager.</p>
 *
 * <p>Note that this rule is designed to be used to set only "primitive"
 * bean properties, eg String, int, boolean. If some of the child xml elements
 * match ObjectCreateRule rules (ie cause objects to be created) then you must
 * use one of the more complex constructors to this rule to explicitly skip
 * processing of that xml element, and define a SetNextRule (or equivalent) to
 * handle assigning the child object to the appropriate property instead.</p>
 *
 * <p><b>Implementation Notes</b></p>
 *
 * <p>This class works by creating its own simple Rules implementation. When
 * begin is invoked on this rule, the digester's current rules object is
 * replaced by a custom one. When end is invoked for this rule, the original
 * rules object is restored. The digester rules objects therefore behave in
 * a stack-like manner.</p>
 *
 * <p>For each child element encountered, the custom Rules implementation
 * ensures that a special AnyChildRule instance is included in the matches
 * returned to the digester, and it is this rule instance that is responsible
 * for setting the appropriate property on the target object (if such a property
 * exists). The effect is therefore like a "trailing wildcard pattern". The
 * custom Rules implementation also returns the matches provided by the
 * underlying Rules implementation for the same pattern, so other rules
 * are not "disabled" during processing of a SetNestedPropertiesRule.</p>
 *
 * <p>TODO: Optimise this class. Currently, each time begin is called,
 * new AnyChildRules and AnyChildRule objects are created. It should be
 * possible to cache these in normal use (though watch out for when a rule
 * instance is invoked re-entrantly!).</p>
 *
 * @since 1.6
 */

public class SetNestedPropertiesRule extends Rule {

    private Log log = null;

    private boolean trimData = true;
    private boolean allowUnknownChildElements = false;

    private HashMap elementNames = new HashMap();

    // ----------------------------------------------------------- Constructors

    /**
     * Base constructor, which maps every child element into a bean property
     * with the same name as the xml element.
     *
     * <p>It is an error if a child xml element exists but the target java
     * bean has no such property (unless setAllowUnknownChildElements has been
     * set to true).</p>
     */
    public SetNestedPropertiesRule() {
        // nothing to set up
    }

    /**
     * <p>Convenience constructor which overrides the default mappings for
     * just one property.</p>
     *
     * <p>For details about how this works, see
     * {@link #SetNestedPropertiesRule(String[] elementNames,
     * String[] propertyNames)}.</p>
     *
     * @param elementName is the child xml element to match
     * @param propertyName is the java bean property to be assigned the value
     * of the specified xml element. This may be null, in which case the
     * specified xml element will be ignored.
     */
    public SetNestedPropertiesRule(String elementName, String propertyName) {
        elementNames.put(elementName, propertyName);
    }

    /**
     * <p>Constructor which allows element->property mapping to be overridden.
     * </p>
     *
     * <p>Two arrays are passed in. One contains xml element names and the
     * other java bean property names. The element name / property name pairs
     * are matched by position; in order words, the first string in the element
     * name array corresponds to the first string in the property name array
     * and so on.</p>
     *
     * <p>If a property name is null or the xml element name has no matching
     * property name due to the arrays being of different lengths then this
     * indicates that the xml element should be ignored.</p>
     *
     * <h5>Example One</h5>
     * <p> The following constructs a rule that maps the <code>alt-city</code>
     * element to the <code>city</code> property and the <code>alt-state</code>
     * to the <code>state</code> property. All other child elements are mapped
     * as usual using exact name matching.
     * <code><pre>
     *      SetNestedPropertiesRule(
     *                new String[] {"alt-city", "alt-state"},
     *                new String[] {"city", "state"});
     * </pre></code>
     * </p>
     *
     * <h5>Example Two</h5>
     * <p> The following constructs a rule that maps the <code>class</code>
     * xml element to the <code>className</code> property. The xml element
     * <code>ignore-me</code> is not mapped, ie is ignored. All other elements
     * are mapped as usual using exact name matching.
     * <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"class", "ignore-me"},
     *                new String[] {"className"});
     * </pre></code>
     * </p>
     *
     * @param elementNames names of elements to map
     * @param propertyNames names of properties mapped to
     */
    public SetNestedPropertiesRule(String[] elementNames, String[] propertyNames) {
        for (int i=0, size=elementNames.length; i<size; i++) {
            String propName = null;
            if (i < propertyNames.length) {
                propName = propertyNames[i];
            }

            this.elementNames.put(elementNames[i], propName);
        }
    }

    // --------------------------------------------------------- Public Methods

    /** Invoked when rule is added to digester. */
    public void setDigester(Digester digester) {
        super.setDigester(digester);
        log = digester.getLogger();
    }

    /**
     * When set to true, any text within child elements will have leading
     * and trailing whitespace removed before assignment to the target
     * object. The default value for this attribute is true.
     */
    public void setTrimData(boolean trimData) {
        this.trimData = trimData;
    }

    /** See {@link #setTrimData}. */
     public boolean getTrimData() {
        return trimData;
    }

    /**
     * Determines whether an error is reported when a nested element is
     * encountered for which there is no corresponding property-setter
     * method.
     * <p>
     * When set to false, any child element for which there is no
     * corresponding object property will cause an error to be reported.
     * <p>
     * When set to true, any child element for which there is no
     * corresponding object property will simply be ignored.
     * <p>
     * The default value of this attribute is false (unknown child elements
     * are not allowed).
     */
    public void setAllowUnknownChildElements(boolean allowUnknownChildElements) {
        this.allowUnknownChildElements = allowUnknownChildElements;
    }

    /** See {@link #setAllowUnknownChildElements}. */
     public boolean getAllowUnknownChildElements() {
        return allowUnknownChildElements;
    }

    /**
     * Process the beginning of this element.
     *
     * @param namespace is the namespace this attribute is in, or null
     * @param name is the name of the current xml element
     * @param attributes is the attribute list of this element
     */
    public void begin(String namespace, String name, Attributes attributes)
                      throws Exception {
        Rules oldRules = digester.getRules();
        AnyChildRule anyChildRule = new AnyChildRule();
        anyChildRule.setDigester(digester);
        AnyChildRules newRules = new AnyChildRules(anyChildRule);
        newRules.init(digester.getMatch()+"/", oldRules);
        digester.setRules(newRules);
    }

    /**
     * This is only invoked after all child elements have been processed,
     * so we can remove the custom Rules object that does the
     * child-element-matching.
     */
    public void body(String bodyText) throws Exception {
        AnyChildRules newRules = (AnyChildRules) digester.getRules();
        digester.setRules(newRules.getOldRules());
    }

    /**
     * Add an additional custom xml-element -> property mapping.
     * <p>
     * This is primarily intended to be used from the xml rules module
     * (as it is not possible there to pass the necessary parameters to the
     * constructor for this class). However it is valid to use this method
     * directly if desired.
     */
    public void addAlias(String elementName, String propertyName) {
        elementNames.put(elementName, propertyName);
    }

    /**
     * Render a printable version of this Rule.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("SetNestedPropertiesRule[");
        sb.append("allowUnknownChildElements=");
        sb.append(allowUnknownChildElements);
        sb.append(", trimData=");
        sb.append(trimData);
        sb.append(", elementNames=");
        sb.append(elementNames);
        sb.append("]");
        return sb.toString();
    }

    //----------------------------------------- local classes

    /** Private Rules implementation */
    private class AnyChildRules implements Rules {
        private String matchPrefix = null;
        private Rules decoratedRules = null;

        private ArrayList rules = new ArrayList(1);
        private AnyChildRule rule;

        public AnyChildRules(AnyChildRule rule) {
            this.rule = rule;
            rules.add(rule);
        }

        public Digester getDigester() { return null; }
        public void setDigester(Digester digester) {}
        public String getNamespaceURI() {return null;}
        public void setNamespaceURI(String namespaceURI) {}
        public void add(String pattern, Rule rule) {}
        public void clear() {}

        public List match(String matchPath) {
            return match(null,matchPath);
        }

        public List match(String namespaceURI, String matchPath) {
            List match = decoratedRules.match(namespaceURI, matchPath);

            if ((matchPath.startsWith(matchPrefix)) &&
                (matchPath.indexOf('/', matchPrefix.length()) == -1)) {

                // The current element is a direct child of the element
                // specified in the init method, so we want to ensure that
                // the rule passed to this object's constructor is included
                // in the returned list of matching rules.

                if ((match == null || match.size()==0)) {
                    // The "real" rules class doesn't have any matches for
                    // the specified path, so we return a list containing
                    // just one rule: the one passed to this object's
                    // constructor.
                    return rules;
                }
                else {
                    // The "real" rules class has rules that match the current
                    // node, so we return this list *plus* the rule passed to
                    // this object's constructor.
                    //
                    // It might not be safe to modify the returned list,
                    // so clone it first.
                    LinkedList newMatch = new LinkedList(match);
                    newMatch.addLast(rule);
                    return newMatch;
                }
            }
            else {
                return match;
            }
        }

        public List rules() {
            // This is not actually expected to be called.
            throw new RuntimeException(
                "AnyChildRules.rules not implemented.");
        }

        public void init(String prefix, Rules rules) {
            matchPrefix = prefix;
            decoratedRules = rules;
        }

        public Rules getOldRules() {
            return decoratedRules;
        }
    }

    private class AnyChildRule extends Rule {
        private String currChildNamespaceURI = null;
        private String currChildElementName = null;

        public void begin(String namespaceURI, String name,
                              Attributes attributes) throws Exception {

            currChildNamespaceURI = namespaceURI;
            currChildElementName = name;
        }

        public void body(String value) throws Exception {
            boolean debug = log.isDebugEnabled();

            String propName = currChildElementName;
            if (elementNames.containsKey(currChildElementName)) {
                // overide propName
                propName = (String) elementNames.get(currChildElementName);
                if (propName == null) {
                    // user wants us to ignore this element
                    return;
                }
            }

            if (digester.log.isDebugEnabled()) {
                digester.log.debug("[SetNestedPropertiesRule]{" + digester.match +
                        "} Setting property '" + propName + "' to '" +
                        value + "'");
            }

            // Populate the corresponding properties of the top object
            Object top = digester.peek();
            if (digester.log.isDebugEnabled()) {
                if (top != null) {
                    digester.log.debug("[SetNestedPropertiesRule]{" + digester.match +
                                       "} Set " + top.getClass().getName() +
                                       " properties");
                } else {
                    digester.log.debug("[SetPropertiesRule]{" + digester.match +
                                       "} Set NULL properties");
                }
            }

            if (trimData) {
                value = value.trim();
            }

            if (!allowUnknownChildElements) {
                // Force an exception if the property does not exist
                // (BeanUtils.setProperty() silently returns in this case)
                if (top instanceof DynaBean) {
                    DynaProperty desc =
                        ((DynaBean) top).getDynaClass().getDynaProperty(propName);
                    if (desc == null) {
                        throw new NoSuchMethodException
                            ("Bean has no property named " + propName);
                    }
                } else /* this is a standard JavaBean */ {
                    PropertyDescriptor desc =
                        PropertyUtils.getPropertyDescriptor(top, propName);
                    if (desc == null) {
                        throw new NoSuchMethodException
                            ("Bean has no property named " + propName);
                    }
                }
            }

            try
            {
            BeanUtils.setProperty(top, propName, value);
            }
            catch(NullPointerException e) {
                digester.log.error("NullPointerException: "
                 + "top=" + top + ",propName=" + propName + ",value=" + value + "!");
                 throw e;
            }
        }

        public void end(String namespace, String name) throws Exception {
            currChildElementName = null;
        }
    }
}
