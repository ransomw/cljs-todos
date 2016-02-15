/*global module, require, process */

const tap_spec = require('tap-spec');
const tape = require('tape');
const Q = require('q');

const st_all = require('./setup_teardown_all');
const st_one = require('./setup_teardown_one');
const tests = require('./tests');

const func_tests = function (t) {
  t.test("setup functional tests", st_all.setup);
  t.test("setup functional test", st_one.setup);
  t.test("displays homepage", tests.test_home);
  t.test("teardown functional test", st_one.teardown);
  t.test("teardown functional tests", st_all.teardown);
  t.ok(true, "placeholder");
  t.end();
};

const run_tests = function () {
  var deferred = Q.defer();
  tape.createStream()
    .pipe(tap_spec())
    .pipe(process.stdout);
  tape.test("functional tests", func_tests);
  tape.onFinish(function () {
    deferred.resolve();
  });
  return deferred.promise;
};

module.exports = run_tests;
