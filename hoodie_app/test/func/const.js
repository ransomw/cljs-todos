/*global require, module, process */

// webdriver port.
// https://www.w3.org/TR/webdriver/
const WD_PORT = process.env.WD_PORT || 7999;
const APP_PORT = process.env.PORT || 5005;

var exports = {};

exports.WD_PORT = WD_PORT;
exports.APP_PORT = APP_PORT;

module.exports = exports;
