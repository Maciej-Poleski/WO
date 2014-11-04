#!/usr/bin/env bash
rm -rf coverage
mkdir coverage
cp correct.cpp coverage/
cd coverage
g++ -Wall -Wextra -fprofile-arcs -ftest-coverage -coverage correct.cpp -o correct
./correct < ../testy/test$1.in > ../testy/test$1.tout
lcov --directory . --capture --output-file app.info
genhtml app.info
konqueror index.html
