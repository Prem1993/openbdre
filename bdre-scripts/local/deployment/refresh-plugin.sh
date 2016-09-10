PLUGIN_HOME=~/BDREPluginStore
if [ -d "$PLUGIN_HOME" ]; then
   echo "deleting directory"
    rm -rf "$PLUGIN_HOME"
fi
echo "cloning repo for first time"
        cd ~
        git clone https://github.com/BDREPlugins/BDREPluginStore.git
        if [ $? -ne 0 ]
            then exit 1
        fi
