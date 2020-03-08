#!/bin/bash
# Adapted from:
# https://github.com/timothypratley/whip/blob/master/deploy.sh

set -e
lein package
cd public
git init
git add .
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:simake/othello.git" master:gh-pages
rm -rf .git
