function SAXParser() {
  this.doc = false; this.handler = false; this.str = '';
  this.cname = new Array(); this.curr = 0;
  this.notToBeParsed_CDATA = { 
    running: false, value: '' 
  };
}
SAXParser.prototype.setDocumentHandler = 
  function(aHandler) {
  this.handler = aHandler;
}
SAXParser.prototype.parse = function(aData) {
  this.str += aData;
  if(this.handler == false) { return; }
  if(!this.doc) {
    var start = this.str.indexOf('<');
    if(this.str.substring(start,start + 3) == '<?x' || 
      this.str.substring(start,start + 3) == '<?X' ) {
      var close = this.str.indexOf('?>');
      if(close == -1) { return; }
      this.str = 
        this.str.substring(close + 2,
        this.str.length);
    }
    this.handler.startDocument();
    this.doc = true;
  }
  while(1) {
    if(this.str.length == 0) { return; }
    if (this.notToBeParsed_CDATA.running) {
      var CDATA_end = this.str.indexOf(']]>');
      if (CDATA_end == -1) {
        this.notToBeParsed_CDATA.string += this.str;
        this.str = '';
        continue;
      } else {
        this.notToBeParsed_CDATA.string += 
          this.str.substring(0, CDATA_end);
        this.str = this.str.substring(
          CDATA_end + 3, this.str.length);
        this.notToBeParsed_CDATA.running = false;
        this.handler.characters(
          this.notToBeParsed_CDATA.string);
        this.notToBeParsed_CDATA.string = '';
        continue;
      }
    }
    var eclose = this.str.indexOf('</' + 
      this.cname[this.curr] + '>');
    if(eclose == 0) {
      this.str = 
        this.str.substring(
        this.cname[this.curr].length + 3, 
        this.str.length);
      this.handler.endElement(this.cname[this.curr])
        this.curr--;
      if(this.curr == 0) {
        this.doc = false;
        this.handler.endDocument();
        return;
      }
      continue;
    }
    if (this.str.indexOf('<![CDATA[') == 0) {
      this.notToBeParsed_CDATA.running = true;
      this.notToBeParsed_CDATA.string = '';
      this.str = this.str.substring(9, this.str.length);
      continue;
    }
    var estart = this.str.indexOf('<');
    if(estart == 0) {
      close = this.indexEndElement(this.str);
      if(close == -1) { return; }
      var empty = 
        (this.str.substring(close - 1,close) == '/');
      if(empty) {
        var starttag = this.str.substring(1,close - 1);
      }else{
        starttag = this.str.substring(1,close);
      }
      var nextspace = starttag.indexOf(' ');
      var attribs = new String();
      var name = new String();
      if(nextspace != -1) {
        name = starttag.substring(0,nextspace);
        attribs = 
          starttag.substring(nextspace + 1,
          starttag.length);
      }else{
        name = starttag;
      }
      this.handler.startElement(name, 
        this.attribution(attribs));
      if(empty) {
        this.handler.endElement(name);
      }else{
        this.curr++;
        this.cname[this.curr] = name;
      }
      this.str = 
        this.str.substring(close + 1,this.str.length);
      continue;
    }
    if(estart == -1) {
      this.handler.characters(this.entityCheck(this.str));
      this.str = '';
    }else{
      this.handler.characters(
        this.entityCheck(this.str.substring(0,estart)));
      this.str = this.str.substring(
        estart,this.str.length);
    }
  }
}
SAXParser.prototype.indexEndElement = function(aStr) {
  var eq = sp = gt = 0;
  sp = aStr.indexOf(' ');
  gt = aStr.indexOf('>');
  if(sp < gt) {
    if(sp == -1) { return gt; }
    if(aStr.charAt(sp+1) == '>') { return sp; }
  } else {
    return gt;
  }
  var end = 0;
  while(1) {
    eq = aStr.indexOf('=', end);
    id = aStr.charAt(eq+1);
    end = aStr.indexOf(id, eq+2);
    if(aStr.charAt(end+1) == '/' && 
      aStr.charAt(end+2) == '>') {
      return end+2;
    }
    if(aStr.charAt(end+1) == '>') { return end+1; }
    end = end+1;
  }
  return end;
}
SAXParser.prototype.attribution = function(aStr) {
  var attribs = new Array();
  var ids = new Number();
  var eq = id1 = id2 = nextid = val = key = new String();
  i = 0;
  while(1) {
    eq = aStr.indexOf('=');
    if(aStr.length == 0 || eq == -1) { return attribs; }
    id1 = aStr.indexOf('\'');
    id2 = aStr.indexOf('\"');
    if((id1 < id2 && id1 != -1) || id2 == -1) {
      ids = id1; id = '\'';
    }
    if((id2 < id1 || id1 == -1) && id2 != -1) {
      ids = id2; id = '\"';
    }
    nextid = aStr.indexOf(id,ids + 1);
    val = aStr.substring(ids + 1,nextid);
    key = aStr.substring(0,eq);
    ws = key.split('\n');
    key = ws.join('');
    ws = key.split(' ');
    key = ws.join('');
    ws = key.split('\t');
    key = ws.join('');
    attribs[i] = key + '=' + this.entityCheck(val);
    i++;
    aStr = aStr.substring(nextid + 1,aStr.length);
  }
  return attribs;
}
SAXParser.prototype.entityCheck = function(aStr) {
  var A = new Array();
  A = aStr.split('&lt;'); aStr = A.join('<');
  A = aStr.split('&gt;'); aStr = A.join('>');
  A = aStr.split('&quot;'); aStr = A.join('\'');
  A = aStr.split('&apos;'); aStr = A.join('\'');
  A = aStr.split('&amp;'); aStr = A.join('&');
  return aStr;
}
function JSDigester() {
  this.EVENT_BEGIN = 1; this.EVENT_BODY = 2; 
  this.EVENT_END = 3;
  this.currentPath = null; this.rules = new Array();
  this.objectStack = []; this.rootObject = null;
  this.saxParser = new SAXParser();
  this.init();
}
JSDigester.prototype.init = function() {
  this.saxParser.setDocumentHandler(this);
}
JSDigester.prototype.parse = function(inXMLString) {
  this.objectStack.splice(0); this.currentPath = '';
  this.rootObject = null;
  this.saxParser.parse(inXMLString);
  return rootObject;
}
JSDigester.prototype.push = function(inObj) {
  this.objectStack.push(inObj);
}
JSDigester.prototype.pop = function() {
  obj = this.objectStack.pop();
  rootObject = obj;
  return obj;
}
JSDigester.prototype.startDocument = function() { }
JSDigester.prototype.startElement = 
  function(inName, inAttributes) {
  if (this.currentPath != '') { this.currentPath += '/'; }
  this.currentPath += inName;
  this.fireRules(this.EVENT_BEGIN, 
    inName, inAttributes, null);
}
JSDigester.prototype.characters = function(inText) {
  this.fireRules(this.EVENT_BODY, null, null, inText);
}
JSDigester.prototype.endElement = function(inName) {
  this.fireRules(this.EVENT_END, inName, null, null);
  i = this.currentPath.lastIndexOf('/');
  this.currentPath = this.currentPath.substr(0, i);
}
JSDigester.prototype.endDocument = function() { }
JSDigester.prototype.fireRules = function 
  (inEvent, inName, inAttributes,
  inText) {
  ruleIndex = null;
  if (inEvent == this.EVENT_BEGIN) {
    ruleIndex = 0;
  } else {
    ruleIndex = this.rules.length - 1;
  }
  for (ruleCounter = 0; ruleCounter < this.rules.length; 
    ruleCounter++) {
    rule = this.rules[ruleIndex];
    if (rule.getPath() == this.currentPath) {
      switch (inEvent) {
        case this.EVENT_BEGIN: 
          rule.begin(inAttributes); 
        break;
        case this.EVENT_BODY: rule.body(inText); break;
        case this.EVENT_END: rule.end(inName); break;
      }
    }
    if (inEvent == this.EVENT_BEGIN) {
      ruleIndex++;
    } else {
      ruleIndex--;
    }
  }
}
JSDigester.prototype.addObjectCreate = 
  function(inPath, inClassName) {
  rule = new ObjectCreateRule(inPath, inClassName, this);
  this.rules.push(rule);
}
JSDigester.prototype.addSetProperties = function(inPath) {
  rule = new SetPropertiesRule(inPath, this);
  this.rules.push(rule);
}
JSDigester.prototype.addBeanPropertySetter = 
  function(inPath, inMethod) {
  rule = new BeanPropertySetterRule(
    inPath, inMethod, this);
  this.rules.push(rule);
}
JSDigester.prototype.addSetNext = 
  function(inPath, inMethod) {
  rule = new SetNextRule(inPath, inMethod, this);
  this.rules.push(rule);
}
function ObjectCreateRule(
  inPath, inClassName, inJSDigester) {
  this.ruleType = 'ObjectCreateRule'; this.path = inPath;
  this.className = inClassName; 
  this.jsDigester = inJSDigester;
}
ObjectCreateRule.prototype.getRuleType = function() {
  return this.ruleType;
}
ObjectCreateRule.prototype.getPath = function() {
  return this.path;
}
ObjectCreateRule.prototype.begin = 
  function(inAttributes) {
  protoObj = eval(this.className); 
  newObj = new protoObj();
  this.jsDigester.push(newObj);
}
ObjectCreateRule.prototype.body = function(inText) { }
ObjectCreateRule.prototype.end = function(inName) {
  this.jsDigester.pop();
}
function SetPropertiesRule(inPath, inJSDigester) {
  this.ruleType = 'SetPropertiesRule';
  this.path = inPath; this.jsDigester = inJSDigester;
}
SetPropertiesRule.prototype.getRuleType = function() {
  return this.ruleType;
}
SetPropertiesRule.prototype.getPath = function() {
  return this.path;
}
SetPropertiesRule.prototype.begin = 
  function(inAttributes) {
  obj = this.jsDigester.pop();
  for (i = 0; i < inAttributes.length; i++) {
    nextAttribute = inAttributes[i];
    keyVal = nextAttribute.split('=');
    key = keyVal[0]; val = keyVal[1];
    key = 'set' + 
      key.substring(0, 1).toUpperCase() + 
      key.substring(1);
    obj[key](val);
  }
  this.jsDigester.push(obj);
}
SetPropertiesRule.prototype.body = function(inText) { }
SetPropertiesRule.prototype.end = function(inName) { }
function BeanPropertySetterRule(
  inPath, inMethod, inJSDigester) {
  this.ruleType = 'BeanPropertySetterRule';
  this.path = inPath; this.setMethod = inMethod;
  this.jsDigester = inJSDigester;
}
BeanPropertySetterRule.prototype.getRuleType = 
  function() {
  return this.ruleType;
}
BeanPropertySetterRule.prototype.getPath = function() {
  return this.path;
}
BeanPropertySetterRule.prototype.begin = 
  function(inAttributes) { }
BeanPropertySetterRule.prototype.body = function(inText) {
  obj = this.jsDigester.pop();
  obj[this.setMethod](inText);
  this.jsDigester.push(obj);
}
BeanPropertySetterRule.prototype.end = 
  function(inName) { }
function SetNextRule(inPath, inMethod, inJSDigester) {
  this.ruleType = 'SetNextRule';
  this.path = inPath; this.setMethod = inMethod;
  this.jsDigester = inJSDigester;
}
SetNextRule.prototype.getRuleType = function() {
  return this.ruleType;
}
SetNextRule.prototype.getPath = function() {
  return this.path;
}
SetNextRule.prototype.begin = function(inAttributes) { }
SetNextRule.prototype.body = function(inText) { }
SetNextRule.prototype.end = function(inName) {
  childObj  = this.jsDigester.pop();
  parentObj = this.jsDigester.pop();
  parentObj[this.setMethod](childObj);
  this.jsDigester.push(parentObj);
  this.jsDigester.push(childObj);
}
