/*global require, module */

const execFile = require('child_process').execFile;

const CONST = require('./const');
// path to chromedriver executable
const CD_PATH = require('chromedriver').path;
const WD_PORT = CONST.WD_PORT;

// webdriver process, a `ChildProcess` object
// https://nodejs.org/api/child_process.html#child_process_class_childprocess
var wd_proc;

const setup_tests = function (t) {
  wd_proc = execFile(
    CD_PATH, ['--url-base=/wd/hub',
              '--port=' + WD_PORT.toString()]);
  t.ok(wd_proc.pid, "browser process started");
  t.end();
};

const teardown_tests = function (t) {
  const close_signal = 'SIGKILL';
  t.plan(2);
  t.ok(wd_proc.pid, "webdriver process running");
  wd_proc.on(
    'exit',
    (code, signal) => {
      t.equal(signal, close_signal,
              "webdriver process exit on expected signal");
    });
  wd_proc.kill(close_signal);
};

console.log("exports");
console.log(exports);

var exports = {};

exports.setup = setup_tests;
exports.teardown = teardown_tests;

module.exports = exports;
