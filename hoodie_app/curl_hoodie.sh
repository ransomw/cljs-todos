#! /bin/sh

WWW_PORT=$1

curl \
    http://localhost:$WWW_PORT/_api/_files/hoodie.js \
    -o www/js/vendor/hoodie.js
