/*
 * Copyright 2005 Frank W. Zammetti
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


package javawebparts.taglib.jstags;


import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that renders the JSDigester.  This is a
 * client-side implementation of the Commons Digester component, quite a bit
 * trimmed down though (i.e., only a few of the most common rules are
 * implemented).
 * <br><br>
 * JSDigester uses the SAXParser code from the Mozilla JSLib library.  It is
 * included here for convenience, with some minor changes required to work
 * for JSDigester, and also to save some space.
 * <br><br>
 * The implemented rules are CreateObject, SetProperties, BeanPropertySetter
 * and SetNext.  Here is a usage example:
 * <br><br>
 * function testJSDigester() {
 * &nbsp;&nbsp;sampleXML  = "&lt;movies numMovies=\"2\"&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "  &lt;movie&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "    &lt;title&gt;Star Wars&lt;/title&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "    &lt;actor gender=\"male\"&gt;Harrison Ford&lt;
 * /actor&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "    &lt;actor gender=\"female\"&gt;
 * Carrie Fisher&lt;/actor&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "  &lt;/movie&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "  &lt;movie&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "    &lt;title&gt;Real Genius&lt;/title&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "    &lt;actor gender=\"male\"&gt;Val Kilmer&lt;
 * /actor&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "  &lt;/movie&gt;\n";<br>
 * &nbsp;&nbsp;sampleXML += "&lt;/movies&gt;";<br>
 * &nbsp;&nbsp;jsDigester = new JSDigester();<br>
 * &nbsp;&nbsp;jsDigester.addObjectCreate("movies", "Movies");<br>
 * &nbsp;&nbsp;jsDigester.addSetProperties("movies");<br>
 * &nbsp;&nbsp;jsDigester.addObjectCreate("movies/movie", "Movie");<br>
 * &nbsp;&nbsp;jsDigester.addBeanPropertySetter("movies/movie/title",
 * "setTitle");<br>
 * &nbsp;&nbsp;jsDigester.addObjectCreate("movies/movie/actor", "Actor");<br>
 * &nbsp;&nbsp;jsDigester.addSetProperties("movies/movie/actor");<br>
 * &nbsp;&nbsp;jsDigester.addBeanPropertySetter("movies/movie/actor",
 * "setName");<br>
 * &nbsp;&nbsp;jsDigester.addSetNext("movies/movie/actor", "addActor");<br>
 * &nbsp;&nbsp;jsDigester.addSetNext("movies/movie", "addMovie");<br>
 * &nbsp;&nbsp;myMovies = jsDigester.parse(sampleXML);<br>
 * }<br>
 * <br><br>
 * (This is directly from the sample app.  This makes use of some custom
 * Javascript "classes", so please see the sample app for full details)
 * <br><br>
 * Because the rendered code is considerably bigger than most of the other
 * tags in the JSTags library, it is not shown here.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class JSDigesterTag extends TagSupport {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.jsp.JspException");
      Class.forName("javax.servlet.jsp.JspWriter");
      Class.forName("javax.servlet.jsp.PageContext");
      Class.forName("javax.servlet.jsp.tagext.TagSupport");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("JSDigesterTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(CreateBookmarkTag.class);


  /**
   * Whether to render the opening and closing script tags around the
   * emitted Javascript.
   */
  private String renderScriptTags;


  /**
   * renderScriptTags mutator.
   *
   * @param inRenderScriptTags renderScriptTags.
   */
  public void setRenderScriptTags(String inRenderScriptTags) {

    renderScriptTags = inRenderScriptTags;

  } // End setRenderScriptTags().


  /**
   * Render the results of the tag.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.info("CreateBookmarkTag.doStartTag()...");

    try {

      JspWriter    out = pageContext.getOut();
      StringBuffer sb  = new StringBuffer(16384);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        sb.append("<script>\n");
      }

      // Render function code, starting with SAXParser.
      renderSAXParser(sb);

      // Now the JSDigester code.
      renderJSDigester(sb);

      // Finally, render the rules.
      renderRules(sb);

      // Render closing </script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        sb.append("</script>\n");
      }

      out.print(sb);

    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }
    return SKIP_BODY;

  } //  End doStartTag()


  /**
   * Renders the SAXParser handler class code.
   *
   * @param sb StringBuffer the output is being built up in.
   */
  private void renderSAXParser(StringBuffer sb) {

    sb.append("function SAXParser() {\n");
    sb.append("  this.doc = false; this.handler = false; this.str = '';\n");
    sb.append("  this.cname = new Array(); this.curr = 0;\n");
    sb.append("  this.notToBeParsed_CDATA = { running: false, value: " +
      "'' };\n");
    sb.append("}\n");
    sb.append("SAXParser.prototype.setDocumentHandler = function(aHandler) " +
      "{\n");
    sb.append("  this.handler = aHandler;\n");
    sb.append("}\n");
    sb.append("SAXParser.prototype.parse = function(aData) {\n");
    sb.append("  this.str += aData;\n");
    sb.append("  if(this.handler == false) { return; }\n");
    sb.append("  if(!this.doc) {\n");
    sb.append("    var start = this.str.indexOf('<');\n");
    sb.append("    if(this.str.substring(start,start + 3) == '<?x' || \n");
    sb.append("      this.str.substring(start,start + 3) == '<?X' ) {\n");
    sb.append("      var close = this.str.indexOf('?>');\n");
    sb.append("      if(close == -1) { return; }\n");
    sb.append("      this.str = \n");
    sb.append("        this.str.substring(close + 2,\n");
    sb.append("        this.str.length);\n");
    sb.append("    }\n");
    sb.append("    this.handler.startDocument();\n");
    sb.append("    this.doc = true;\n");
    sb.append("  }\n");
    sb.append("  while(1) {\n");
    sb.append("    if(this.str.length == 0) { return; }\n");
    sb.append("    if (this.notToBeParsed_CDATA.running) {\n");
    sb.append("      var CDATA_end = this.str.indexOf(']]>');\n");
    sb.append("      if (CDATA_end == -1) {\n");
    sb.append("        this.notToBeParsed_CDATA.string += this.str;\n");
    sb.append("        this.str = '';\n");
    sb.append("        continue;\n");
    sb.append("      } else {\n");
    sb.append("        this.notToBeParsed_CDATA.string += \n");
    sb.append("          this.str.substring(0, CDATA_end);\n");
    sb.append("        this.str = this.str.substring(\n");
    sb.append("          CDATA_end + 3, this.str.length);\n");
    sb.append("        this.notToBeParsed_CDATA.running = false;\n");
    sb.append("        this.handler.characters(\n");
    sb.append("          this.notToBeParsed_CDATA.string);\n");
    sb.append("        this.notToBeParsed_CDATA.string = '';\n");
    sb.append("        continue;\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("    var eclose = this.str.indexOf('</' + \n");
    sb.append("      this.cname[this.curr] + '>');\n");
    sb.append("    if(eclose == 0) {\n");
    sb.append("      this.str = \n");
    sb.append("        this.str.substring(\n");
    sb.append("        this.cname[this.curr].length + 3, \n");
    sb.append("        this.str.length);\n");
    sb.append("      this.handler.endElement(this.cname[this.curr])\n");
    sb.append("        this.curr--;\n");
    sb.append("      if(this.curr == 0) {\n");
    sb.append("        this.doc = false;\n");
    sb.append("        this.handler.endDocument();\n");
    sb.append("        return;\n");
    sb.append("      }\n");
    sb.append("      continue;\n");
    sb.append("    }\n");
    sb.append("    if (this.str.indexOf('<![CDATA[') == 0) {\n");
    sb.append("      this.notToBeParsed_CDATA.running = true;\n");
    sb.append("      this.notToBeParsed_CDATA.string = '';\n");
    sb.append("      this.str = this.str.substring(9, this.str.length);\n");
    sb.append("      continue;\n");
    sb.append("    }\n");
    sb.append("    var estart = this.str.indexOf('<');\n");
    sb.append("    if(estart == 0) {\n");
    sb.append("      close = this.indexEndElement(this.str);\n");
    sb.append("      if(close == -1) { return; }\n");
    sb.append("      var empty = \n");
    sb.append("        (this.str.substring(close - 1,close) == '/');\n");
    sb.append("      if(empty) {\n");
    sb.append("        var starttag = this.str.substring(1,close - 1);\n");
    sb.append("      }else{\n");
    sb.append("        starttag = this.str.substring(1,close);\n");
    sb.append("      }\n");
    sb.append("      var nextspace = starttag.indexOf(' ');\n");
    sb.append("      var attribs = new String();\n");
    sb.append("      var name = new String();\n");
    sb.append("      if(nextspace != -1) {\n");
    sb.append("        name = starttag.substring(0,nextspace);\n");
    sb.append("        attribs = \n");
    sb.append("          starttag.substring(nextspace + 1,\n");
    sb.append("          starttag.length);\n");
    sb.append("      }else{\n");
    sb.append("        name = starttag;\n");
    sb.append("      }\n");
    sb.append("      this.handler.startElement(name, \n");
    sb.append("        this.attribution(attribs));\n");
    sb.append("      if(empty) {\n");
    sb.append("        this.handler.endElement(name);\n");
    sb.append("      }else{\n");
    sb.append("        this.curr++;\n");
    sb.append("        this.cname[this.curr] = name;\n");
    sb.append("      }\n");
    sb.append("      this.str = \n");
    sb.append("        this.str.substring(close + 1,this.str.length);\n");
    sb.append("      continue;\n");
    sb.append("    }\n");
    sb.append("    if(estart == -1) {\n");
    sb.append("      this.handler.characters(this.entityCheck(this.str));\n");
    sb.append("      this.str = '';\n");
    sb.append("    }else{\n");
    sb.append("      this.handler.characters(\n");
    sb.append("        this.entityCheck(this.str.substring(0,estart)));\n");
    sb.append("      this.str = this.str.substring(estart,this.str." +
      "length);\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("}\n");
    sb.append("SAXParser.prototype.indexEndElement = function(aStr) {\n");
    sb.append("  var eq = sp = gt = 0;\n");
    sb.append("  sp = aStr.indexOf(' ');\n");
    sb.append("  gt = aStr.indexOf('>');\n");
    sb.append("  if(sp < gt) {\n");
    sb.append("    if(sp == -1) { return gt; }\n");
    sb.append("    if(aStr.charAt(sp+1) == '>') { return sp; }\n");
    sb.append("  } else {\n");
    sb.append("    return gt;\n");
    sb.append("  }\n");
    sb.append("  var end = 0;\n");
    sb.append("  while(1) {\n");
    sb.append("    eq = aStr.indexOf('=', end);\n");
    sb.append("    id = aStr.charAt(eq+1);\n");
    sb.append("    end = aStr.indexOf(id, eq+2);\n");
    sb.append("    if(aStr.charAt(end+1) == '/' && \n");
    sb.append("      aStr.charAt(end+2) == '>') {\n");
    sb.append("      return end+2;\n");
    sb.append("    }\n");
    sb.append("    if(aStr.charAt(end+1) == '>') { return end+1; }\n");
    sb.append("    end = end+1;\n");
    sb.append("  }\n");
    sb.append("  return end;\n");
    sb.append("}\n");
    sb.append("SAXParser.prototype.attribution = function(aStr) {\n");
    sb.append("  var attribs = new Array();\n");
    sb.append("  var ids = new Number();\n");
    sb.append("  var eq = id1 = id2 = nextid = val = key = new String();\n");
    sb.append("  i = 0;\n");
    sb.append("  while(1) {\n");
    sb.append("    eq = aStr.indexOf('=');\n");
    sb.append("    if(aStr.length == 0 || eq == -1) { return attribs; }\n");
    sb.append("    id1 = aStr.indexOf('\\'');\n");
    sb.append("    id2 = aStr.indexOf('\\\"');\n");
    sb.append("    if((id1 < id2 && id1 != -1) || id2 == -1) {\n");
    sb.append("      ids = id1; id = '\\'';\n");
    sb.append("    }\n");
    sb.append("    if((id2 < id1 || id1 == -1) && id2 != -1) {\n");
    sb.append("      ids = id2; id = '\\\"';\n");
    sb.append("    }\n");
    sb.append("    nextid = aStr.indexOf(id,ids + 1);\n");
    sb.append("    val = aStr.substring(ids + 1,nextid);\n");
    sb.append("    key = aStr.substring(0,eq);\n");
    sb.append("    ws = key.split('\\n');\n");
    sb.append("    key = ws.join('');\n");
    sb.append("    ws = key.split(' ');\n");
    sb.append("    key = ws.join('');\n");
    sb.append("    ws = key.split('\\t');\n");
    sb.append("    key = ws.join('');\n");
    sb.append("    attribs[i] = key + '=' + this.entityCheck(val);\n");
    sb.append("    i++;\n");
    sb.append("    aStr = aStr.substring(nextid + 1,aStr.length);\n");
    sb.append("  }\n");
    sb.append("  return attribs;\n");
    sb.append("}\n");
    sb.append("SAXParser.prototype.entityCheck = function(aStr) {\n");
    sb.append("  var A = new Array();\n");
    sb.append("  A = aStr.split('&lt;'); aStr = A.join('<');\n");
    sb.append("  A = aStr.split('&gt;'); aStr = A.join('>');\n");
    sb.append("  A = aStr.split('&quot;'); aStr = A.join('\\'');\n");
    sb.append("  A = aStr.split('&apos;'); aStr = A.join('\\'');\n");
    sb.append("  A = aStr.split('&amp;'); aStr = A.join('&');\n");
    sb.append("  return aStr;\n");
    sb.append("}\n");

  } // End renderSAXParser().


  /**
   * Renders the JSDigester class code.
   *
   * @param sb StringBuffer the output is being built up in.
   */
  private void renderJSDigester(StringBuffer sb) {

    sb.append("function JSDigester() {\n");
    sb.append("  this.EVENT_BEGIN = 1; this.EVENT_BODY = 2; \n");
    sb.append("  this.EVENT_END = 3;\n");
    sb.append("  this.currentPath = null; this.rules = new Array();\n");
    sb.append("  this.objectStack = []; this.rootObject = null;\n");
    sb.append("  this.saxParser = new SAXParser();\n");
    sb.append("  this.init();\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.init = function() {\n");
    sb.append("  this.saxParser.setDocumentHandler(this);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.parse = function(inXMLString) {\n");
    sb.append("  this.objectStack.splice(0); this.currentPath = '';\n");
    sb.append("  this.rootObject = null;\n");
    sb.append("  this.saxParser.parse(inXMLString);\n");
    sb.append("  return rootObject;\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.push = function(inObj) {\n");
    sb.append("  this.objectStack.push(inObj);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.pop = function() {\n");
    sb.append("  obj = this.objectStack.pop();\n");
    sb.append("  rootObject = obj;\n");
    sb.append("  return obj;\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.startDocument = function() { }\n");
    sb.append("JSDigester.prototype.startElement = \n");
    sb.append("  function(inName, inAttributes) {\n");
    sb.append("  if (this.currentPath != '') { this.currentPath += '/'; }\n");
    sb.append("  this.currentPath += inName;\n");
    sb.append("  this.fireRules(this.EVENT_BEGIN, \n");
    sb.append("    inName, inAttributes, null);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.characters = function(inText) {\n");
    sb.append("  this.fireRules(this.EVENT_BODY, null, null, inText);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.endElement = function(inName) {\n");
    sb.append("  this.fireRules(this.EVENT_END, inName, null, null);\n");
    sb.append("  i = this.currentPath.lastIndexOf('/');\n");
    sb.append("  this.currentPath = this.currentPath.substr(0, i);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.endDocument = function() { }\n");
    sb.append("JSDigester.prototype.fireRules = function \n");
    sb.append("  (inEvent, inName, inAttributes,\n");
    sb.append("  inText) {\n");
    sb.append("  ruleIndex = null;\n");
    sb.append("  if (inEvent == this.EVENT_BEGIN) {\n");
    sb.append("    ruleIndex = 0;\n");
    sb.append("  } else {\n");
    sb.append("    ruleIndex = this.rules.length - 1;\n");
    sb.append("  }\n");
    sb.append("  for (ruleCounter = 0; ruleCounter < this.rules.length; \n");
    sb.append("    ruleCounter++) {\n");
    sb.append("    rule = this.rules[ruleIndex];\n");
    sb.append("    if (rule.getPath() == this.currentPath) {\n");
    sb.append("      switch (inEvent) {\n");
    sb.append("        case this.EVENT_BEGIN: \n");
    sb.append("          rule.begin(inAttributes); \n");
    sb.append("        break;\n");
    sb.append("        case this.EVENT_BODY: rule.body(inText); break;\n");
    sb.append("        case this.EVENT_END: rule.end(inName); break;\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("    if (inEvent == this.EVENT_BEGIN) {\n");
    sb.append("      ruleIndex++;\n");
    sb.append("    } else {\n");
    sb.append("      ruleIndex--;\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.addObjectCreate = \n");
    sb.append("  function(inPath, inClassName) {\n");
    sb.append("  rule = new ObjectCreateRule(inPath, inClassName, this);\n");
    sb.append("  this.rules.push(rule);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.addSetProperties = function(inPath) {\n");
    sb.append("  rule = new SetPropertiesRule(inPath, this);\n");
    sb.append("  this.rules.push(rule);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.addBeanPropertySetter = \n");
    sb.append("  function(inPath, inMethod) {\n");
    sb.append("  rule = new BeanPropertySetterRule(inPath, inMethod, " +
      "this);\n");
    sb.append("  this.rules.push(rule);\n");
    sb.append("}\n");
    sb.append("JSDigester.prototype.addSetNext = function(inPath, " +
      "inMethod) {\n");
    sb.append("  rule = new SetNextRule(inPath, inMethod, this);\n");
    sb.append("  this.rules.push(rule);\n");
    sb.append("}\n");

  } // End renderJSDigester().


  /**
   * Renders the Digester rule classes.
   *
   * @param sb StringBuffer the output is being built up in.
   */
  private void renderRules(StringBuffer sb) {

    sb.append("function ObjectCreateRule(inPath, inClassName, " +
      "inJSDigester) {\n");
    sb.append("  this.ruleType = 'ObjectCreateRule'; this.path = inPath;\n");
    sb.append("  this.className = inClassName; \n");
    sb.append("  this.jsDigester = inJSDigester;\n");
    sb.append("}\n");
    sb.append("ObjectCreateRule.prototype.getRuleType = function() {\n");
    sb.append("  return this.ruleType;\n");
    sb.append("}\n");
    sb.append("ObjectCreateRule.prototype.getPath = function() {\n");
    sb.append("  return this.path;\n");
    sb.append("}\n");
    sb.append("ObjectCreateRule.prototype.begin = " +
      "function(inAttributes) {\n");
    sb.append("  protoObj = eval(this.className); newObj = " +
      "new protoObj();\n");
    sb.append("  this.jsDigester.push(newObj);\n");
    sb.append("}\n");
    sb.append("ObjectCreateRule.prototype.body = function(inText) { }\n");
    sb.append("ObjectCreateRule.prototype.end = function(inName) {\n");
    sb.append("  this.jsDigester.pop();\n");
    sb.append("}\n");
    sb.append("function SetPropertiesRule(inPath, inJSDigester) {\n");
    sb.append("  this.ruleType = 'SetPropertiesRule';\n");
    sb.append("  this.path = inPath; this.jsDigester = inJSDigester;\n");
    sb.append("}\n");
    sb.append("SetPropertiesRule.prototype.getRuleType = function() {\n");
    sb.append("  return this.ruleType;\n");
    sb.append("}\n");
    sb.append("SetPropertiesRule.prototype.getPath = function() {\n");
    sb.append("  return this.path;\n");
    sb.append("}\n");
    sb.append("SetPropertiesRule.prototype.begin = " +
      "function(inAttributes) {\n");
    sb.append("  obj = this.jsDigester.pop();\n");
    sb.append("  for (i = 0; i < inAttributes.length; i++) {\n");
    sb.append("    nextAttribute = inAttributes[i];\n");
    sb.append("    keyVal = nextAttribute.split('=');\n");
    sb.append("    key = keyVal[0]; val = keyVal[1];\n");
    sb.append("    key = 'set' + \n");
    sb.append("      key.substring(0, 1).toUpperCase() + \n");
    sb.append("      key.substring(1);\n");
    sb.append("    obj[key](val);\n");
    sb.append("  }\n");
    sb.append("  this.jsDigester.push(obj);\n");
    sb.append("}\n");
    sb.append("SetPropertiesRule.prototype.body = function(inText) { }\n");
    sb.append("SetPropertiesRule.prototype.end = function(inName) { }\n");
    sb.append("function BeanPropertySetterRule(\n");
    sb.append("  inPath, inMethod, inJSDigester) {\n");
    sb.append("  this.ruleType = 'BeanPropertySetterRule';\n");
    sb.append("  this.path = inPath; this.setMethod = inMethod;\n");
    sb.append("  this.jsDigester = inJSDigester;\n");
    sb.append("}\n");
    sb.append("BeanPropertySetterRule.prototype.getRuleType = " +
      "function() {\n");
    sb.append("  return this.ruleType;\n");
    sb.append("}\n");
    sb.append("BeanPropertySetterRule.prototype.getPath = function() {\n");
    sb.append("  return this.path;\n");
    sb.append("}\n");
    sb.append("BeanPropertySetterRule.prototype.begin = \n");
    sb.append("  function(inAttributes) { }\n");
    sb.append("BeanPropertySetterRule.prototype.body = function(inText) {\n");
    sb.append("  obj = this.jsDigester.pop();\n");
    sb.append("  obj[this.setMethod](inText);\n");
    sb.append("  this.jsDigester.push(obj);\n");
    sb.append("}\n");
    sb.append("BeanPropertySetterRule.prototype.end = " +
      "function(inName) { }\n");
    sb.append("function SetNextRule(inPath, inMethod, inJSDigester) {\n");
    sb.append("  this.ruleType = 'SetNextRule';\n");
    sb.append("  this.path = inPath; this.setMethod = inMethod;\n");
    sb.append("  this.jsDigester = inJSDigester;\n");
    sb.append("}\n");
    sb.append("SetNextRule.prototype.getRuleType = function() {\n");
    sb.append("  return this.ruleType;\n");
    sb.append("}\n");
    sb.append("SetNextRule.prototype.getPath = function() {\n");
    sb.append("  return this.path;\n");
    sb.append("}\n");
    sb.append("SetNextRule.prototype.begin = function(inAttributes) { }\n");
    sb.append("SetNextRule.prototype.body = function(inText) { }\n");
    sb.append("SetNextRule.prototype.end = function(inName) {\n");
    sb.append("  childObj  = this.jsDigester.pop();\n");
    sb.append("  parentObj = this.jsDigester.pop();\n");
    sb.append("  parentObj[this.setMethod](childObj);\n");
    sb.append("  this.jsDigester.push(parentObj);\n");
    sb.append("  this.jsDigester.push(childObj);\n");
    sb.append("}\n");

  } // End renderRules().


} // End class.
