function JWPAlertLogger() {

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
    alert(msg);
  }
}
