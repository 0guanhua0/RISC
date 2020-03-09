#!/bin/bash

cd shared
emacs --batch -u `whoami` --script scripts/docov.el

cv=`egrep "\| *Totals *\|" coverage.txt | cut -f 3 -d"|" | tr -d " "`

echo "TOTAL COVERAGE: ${cv}%"
cd ..

cd client
emacs --batch -u `whoami` --script scripts/docov.el

cv=`egrep "\| *Totals *\|" coverage.txt | cut -f 3 -d"|" | tr -d " "`

echo "TOTAL COVERAGE: ${cv}%"
cd ..

cd server
emacs --batch -u `whoami` --script scripts/docov.el

cv=`egrep "\| *Totals *\|" coverage.txt | cut -f 3 -d"|" | tr -d " "`

echo "TOTAL COVERAGE: ${cv}%"
cd ..