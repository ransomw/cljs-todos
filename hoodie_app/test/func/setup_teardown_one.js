/*global require, module, process */

const fs = require('fs');

const Q = require('q');
const tmp = require('tmp');
const webdriverio = require('webdriverio');

const app = require('hoodie-server');

const CONST = require('./const');
const WD_PORT = CONST.WD_PORT;
const APP_PORT = CONST.APP_PORT;

var data_dir;

const client = webdriverio.remote({
  host: 'localhost',
  port: WD_PORT,
  desiredCapabilities: { browserName: 'chrome' }
});

const setup_test = function (t) {
  data_dir = tmp.dirSync();
  Q().then(function () {
    var deferred = Q.defer();
    var env_config = {
      www_port: APP_PORT,
      admin_password: 'admin',
      hoodie: {
        data_path: data_dir.name
      }
    };
    app.start(env_config, function (err) {
      if (err) {
        deferred.reject(err);
      } else {
        deferred.resolve();
      }
    });
    return deferred.promise;
  }).then(function () {
    return client.init();
  }).catch(function (err) {
    t.notOk(err, "setup with no errors");
  }).finally(function () {
    t.end();
  });
};

const teardown_test = function (t) {
  Q().then(function () {
    return client.end();
  }).then(function () {
    // TODO
    console.log("hoodie-server stop unimplemented");
    /*
    var deferred = Q.defer();
    app_server.close((err) => {
      if (err) {
        deferred.reject(err);
      } else {
        deferred.resolve();
      }
    });
    return deferred.promise;
     */
  }).then(function () {
    /* fs.unlinkSync(data_dir.name); */
  }).catch(function (err) {
    t.notOk(err, "teardown with no errors");
  }).finally(function () {
    t.end();
  });
};

var exports = {};

exports.setup = setup_test;
exports.teardown = teardown_test;
exports.client = client;

tmp.setGracefulCleanup();

module.exports = exports;
