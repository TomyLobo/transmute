#!/bin/bash

#MESSAGE="Release $1"

#TODO: build.xml, mcmod.info, src/eu/tomylobo/transmute/TransmuteMod.java
git commit -m "Release $1" build.xml mcmod.info src/eu/tomylobo/transmute/TransmuteMod.java && git tag -am "Release $1" $1
