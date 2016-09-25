#!/bin/bash
JIRA=$1

if [ ! "$JIRA" ]; then
    echo "usage: $(basename $0) ISIS-nnn"
    exit 1
fi

export ISISART=$(basename $(pwd))-archetype

TOFIX=""

env | grep ISISTMP >/dev/null
if [ $? -ne 0 ]; then
    echo "\$ISISTMP not set!"
    TOFIX="$TOFIX\nexport ISISTMP=/c/tmp"
fi

env | grep ISISDEV >/dev/null
if [ $? -ne 0 ]; then
    echo "\$ISISDEV not set!"
    TOFIX="$TOFIX\nexport ISISDEV=1.7.0-SNAPSHOT"
fi

env | grep ISISREL >/dev/null
if [ $? -ne 0 ]; then
    echo "\$ISISREL not set!"
    TOFIX="$TOFIX\nexport ISISDEV=1.6.0"
fi

env | grep ISISRC >/dev/null
if [ $? -ne 0 ]; then
    echo "\$ISISRC  not set!"
    TOFIX="$TOFIX\nexport ISISRC=RC1"
fi

env | grep ISISPAR >/dev/null
if [ $? -ne 0 ]; then
    echo "\$ISISPAR not set! (Isis parent release, usually same as ISISREL)"
    TOFIX="$TOFIX\nexport ISISPAR=$ISISREL"
fi

if [ "$TOFIX" != "" ]; then
    echo -e $TOFIX
    exit 1
fi

export ISISCPT=$(echo $ISISART | cut -d- -f2)
export ISISCPN=$(echo $ISISART | cut -d- -f1)

#
#
#
env | grep ISIS | sort


echo "mvn clean ..."
mvn clean

echo "removing other non-source files ..."
for a in .project .classpath .settings bin .idea neo4j_DB target-ide; do /bin/find . -name $a -exec rm -r {} \;; done
/bin/find . -name "*.iml" -exec rm {} \;
/bin/find . -name "*.log" -exec rm {} \;
/bin/find . -name "pom.xml.*" -exec rm {} \;

echo "mvn archetype:create-from-project ..."
mvn archetype:create-from-project

echo "groovy script to update archetypes ..."
groovy ../../../scripts/updateGeneratedArchetypeSources.groovy -n $ISISCPN -v $ISISPAR

echo "deleting old archetype ..."
git rm -rf ../../archetype/$ISISCPN
rm -rf ../../archetype/$ISISCPN
mkdir -p ../../archetype


echo "adding new archetype ..."
ls target/generated-sources/archetype 
ls  ../../archetype/$ISISCPN

mv target/generated-sources/archetype ../../archetype/$ISISCPN
git add ../../archetype/$ISISCPN
git commit -m "$JIRA: recreating $ISISCPN archetype"

