/*global require, module */

const waits = require('./waits');

var exports = {};

exports.wait_until_load_init = waits.make_wait_invisible(
  '[class*=\'spin-\']'
);

module.exports = exports;
