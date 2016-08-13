BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
PLUGIN_HOME=~/pluginappstore
INSTALLED_PLUGIN_HOME=~/bdreplugins

mkdir -p ~/bdreplugins

echo $INSTALLED_PLUGIN_HOME
cd ~/bdreplugins
git clone https://github.com/Prem1993/R-action.git
cd ~/bdreplugins/R-action
#mvn -s settings.xml clean install -P cdh52
cd ~/bdreplugins/R-action/target/
for i in *
do
ZIPNAME=$i
done

zip -r  ./*   ./*
RETURNEDZIPPATH=~/bdreplugins/R-action/target/$ZIPNAME
echo $RETURNEDZIPPATH

