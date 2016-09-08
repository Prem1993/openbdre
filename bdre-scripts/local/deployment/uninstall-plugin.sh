#creating uninstall plugin command for
pluginUniqueId=$1
echo $pluginUniqueId
java -cp "/home/cloudera/bdre/lib/plugin-manager/*" com.wipro.ats.bdre.pm.UninstallPluginMain $pluginUniqueId