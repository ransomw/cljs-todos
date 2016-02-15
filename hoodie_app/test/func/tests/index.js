/*global require, module, process */

const Q = require('q');

const CONST = require('../const');
const APP_PORT = CONST.APP_PORT;
const APP_URL = 'http://localhost:' + APP_PORT;
const PAGELOAD_TIMEOUT = 10000; // ms
const client = require('../setup_teardown_one').client;
const wait_until_load_init = require('../util').wait_until_load_init;

const nav_home = function () {
  return Q().then(function () {
    return client.url(APP_URL)
      .waitUntil(wait_until_load_init, PAGELOAD_TIMEOUT);
  });
};

const test_home = function (t) {
  nav_home().then(function () {
    return client.getTitle();
  }).then(function (title) {
    t.equal(title, 'todos', "displays page title");
  }).catch(function (err) {
    t.notOk(err, "homepage test with no errors");
  }).finally(function () {
    t.end();
  });
};

var exports = {};

exports.test_home = test_home;

module.exports = exports;
