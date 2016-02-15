/*global require, module */

const _ = require('lodash');
const Q = require('q');

const make_wait_invisible = function (selector) {
  const get_el_props = function (el_id) {
    return Q.all([
      self.elementIdLocationInView(el_id),
      self.elementIdSize(el_id),
      self.elementIdDisplayed(el_id),
      self.elementIdAttribute(el_id, 'class')
    ]).spread(function (loc, size, disp, cls) {
      return {location: loc.value,
              size: {
                h: size.value.height,
                w: size.value.width},
              disp: disp.value,
              cls: cls.value // for debug
             };
    }).catch(function (err) {
      return null;
    });
  };
  const is_visible_el_info = function (el_info) {
    if (el_info === null) {
      return false; // stale WebElement, wait some more
    }
    return (el_info.size.h === 0 && el_info.size.w === 0 &&
            el_info.location.x === 0 && el_info.location.y === 0) ||
      !el_info.disp;
  };
  return function () {
    return this.elements(selector)
      .then(function (res) {
        var self = this;
        if (!res.value) {
          return [];
        }
        return Q.all(_.map(res.value, 'ELEMENT').map(get_el_props));
      }).then(function (el_infos) {
        var el_info_visibilities = el_infos.map(is_visible_el_info);
        return el_info_visibilities
          .reduce(function (a, b) {
            return a && b;
          }, true);
      });
  };
};


var exports = {};

exports.make_wait_invisible = make_wait_invisible;

module.exports = exports;
