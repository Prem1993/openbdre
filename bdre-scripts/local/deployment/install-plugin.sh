BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
PLUGIN_HOME=~/pluginappstore
INSTALLED_PLUGIN_HOME=~/bdreplugins

mkdir -p ~/bdreplugins
echo $INSTALLED_PLUGIN_HOME
cd $INSTALLED_PLUGIN_HOME
if [ -d "$2" ]; then
    echo "refreshing plugin repo"
    cd $2
    git pull origin master
    if [ $? -ne 0 ]
        then exit 1
    fi

else
echo "cloning plugin for first time"
git clone $1
if [ $? -ne 0 ]
then exit 1
fi
cd ~/bdreplugins/$2
fi
if [ $? -ne 0 ]
then exit 1
fi
mvn -s settings.xml clean install -P cdh52
if [ $? -ne 0 ]
then exit 1
fi
cd ~/bdreplugins/$2/target/
if [ $? -ne 0 ]
then exit 1
fi
for i in *
do
ZIPNAME=$i
done

zip -r  ./*   ./*
if [ $? -ne 0 ]
then exit 1
fi
RETURNEDZIPPATH=~/bdreplugins/$2/target/$ZIPNAME".zip"
echo $RETURNEDZIPPATH
#creating plugin command for
java -cp "/home/cloudera/bdre/lib/plugin-manager/*" com.wipro.ats.bdre.pm.PluginManagerMain -p $RETURNEDZIPPATH
if [ $? -ne 0 ]
then exit 1
fi