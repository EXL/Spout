#!/bin/bash
rm ./spout.pnd
mksquashfs ./spout ./readme.pandora.txt ./PXML.xml ./spout.png spout.sfs
cat ./spout.sfs ./PXML.xml ./spout.png > spout.pnd
rm ./spout.sfs
