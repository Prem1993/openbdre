#!/usr/bin/env bash
BDRE_HOME=~/bdre
. $BDRE_HOME/bdre-scripts/env.properties
BDRE_PLUGINSTORE_REPO=~/BDREPluginStore
#Pull latest plugins from the repo
if [ -d "$BDRE_PLUGINSTORE_REPO" ]; then
    echo "refresing repo"
    cd BDRE_PLUGINSTORE_REPO
    git pull origin  master
    if [ $? -ne 0 ]
        then exit 1
    fi
else
     echo "cloning repo for first time"
        cd ~
        git clone https://github.com/BDREPlugins/BDREPluginStore.git
        if [ $? -ne 0 ]
            then exit 1
        fi
        cd $BDRE_PLUGINSTORE_REPO
fi
echo 'done'
