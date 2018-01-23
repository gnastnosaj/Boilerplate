cordova.define("cordova-plugin-channel.Channel", function(require, exports, module) {

function Channel() {
  this.callbacks = {};
}

Channel.prototype.exec = function(scheme, data, onNext, onError, onComplete) {
  let callbackId = null;
  if (this.callbackId) {
    callbackId = this.callbackId + 1;
  } else {
    callbackId = Math.floor(Math.random() * 2000000000);
  }
  this.callbackId = callbackId;
  callbackId = '' + callbackId;
  this.callbacks[callbackId] = {
    "onNext": onNext,
    "onError": onError,
    "onComplete": onComplete
  };

  let timeout = 3000;
  if (arguments.length == 6) {
    try {
      timeout = arguments[5];
    } catch (e) {
        console.error(e);
    }
  }
  let execTimeout = setTimeout(() => {
    try {
      if (this.callbacks[callbackId]) {
        delete this.callbacks[callbackId];
        if (onError) {
          onError({
            'ERROR_MSG': 'timeout'
          });
        }
      }
    } catch (e) {
      console.error(e);
    }
  }, timeout);

  let _this = this;
  cordova.exec(function(data) {
    clearTimeout(execTimeout);
    try {
      if (_this.callbacks[callbackId]) {
        if(data["__channel__keep__"] && _this.callbacks[callbackId].onNext) {
            _this.callbacks[callbackId].onNext(data);
        }else if(_this.callbacks[callbackId].onComplete) {
            _this.callbacks[callbackId].onComplete();
        }
      }
    } catch (e) {
      Logger.error(service, e);
    }
  }, function(data) {
    clearTimeout(execTimeout);
    try {
      if (_this.callbacks[callbackId] && _this.callbacks[callbackId].onError) {
        _this.callbacks[callbackId].onError(data);
      }
    } catch (e) {
      Logger.error(service, e);
    }
  }, "Channel", "exec", [scheme, data]);

  return callbackId;
};

Channel.prototype.cancel = function(callbackId) {
  if (this.callbacks[callbackId]) {
    delete this.callbacks[callbackId];
  }
};

Channel.prototype.subscribe = function(tag, onNext, onError) {
  cordova.exec(onNext, onError, "Channel", "subscribe");
};

Channel.prototype.dispose = function(tag, onNext, onError) {
  cordova.exec(onNext, onError, "Channel", "dispose");
};

Channel.prototype.register = function(tag, onNext, onError) {
  cordova.exec(onNext, onError, "Channel", "register", [tag]);
};

Channel.prototype.unregister = function(tag, onNext, onError) {
  cordova.exec(onNext, onError, "Channel", "unregister", [tag]);
};

module.exports = new Channel();

});