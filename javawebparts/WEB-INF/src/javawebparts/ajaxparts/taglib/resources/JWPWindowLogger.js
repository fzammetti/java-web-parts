function JWPWindowLogger() {

  this.LEVEL_TRACE = 0;
  this.LEVEL_DEBUG = 1;
  this.LEVEL_INFO = 2;
  this.LEVEL_ERROR = 3;
  this.LEVEL_FATAL = 4;
  this.handle = null;
  this.minLevel = 2;
  this.bg = ["#ffff00","#00ffff","#00ff00","#ffa0a0","#ff0000"];
  this.setMinLevel = function(level) {
    minLevel = level;
  }
  this.trace = function(msg) {
    if (minLevel <= this.LEVEL_TRACE) {
      this.write(msg, this.bg[this.LEVEL_TRACE]);
    }
  }
  this.debug = function(msg) {
    if (minLevel <= this.LEVEL_DEBUG) {
      this.write(msg, this.bg[this.LEVEL_DEBUG]);
    }
  }
  this.info = function(msg) {
    if (minLevel <= this.LEVEL_INFO) {
      this.write(msg, this.bg[this.LEVEL_INFO]);
    }
  }
  this.error = function(msg) {
    if (minLevel <= this.LEVEL_ERROR) {
      this.write(msg, this.bg[this.LEVEL_ERROR]);
    }
  }
  this.fatal = function(msg) {
    if (minLevel <= this.LEVEL_FATAL) {
      this.write(msg, this.bg[this.LEVEL_FATAL]);
    }
  }
  this.write = function(msg, bg) {
    try {
      this.handle.document.write("<div style=\"background: " + bg +
        ";\"><pre>" + msg + "</pre></div>");
    } catch(error) {
      this.handle = null;
      this.handle = open("", "JWPWindowLogger",
        "width=600,height=400,resizable=1,menubar=0,scrollbars=1");
      this.handle.document.open();
      this.write(msg, bg);
    }
  }
}
