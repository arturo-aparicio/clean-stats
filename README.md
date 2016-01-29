# clean-stats

## What is this?

The clean stats	plugin is to be used with a modified Artifactory MySQL stats table. This plugin is not meant for general use. The plugin will clean the stats table, removing any obsolete tasks.

### Precondition:

1. Shutdown Artifactory
2. Connect to the Artifactory database
3. Execute the following commands:
```
CONNECT {DB_NAME};
ALTER TABLE stats DROP FOREIGN KEY stats_nodes_fk;	
```

### Installation:

1. Follow the plugin installation instructions here: https://www.jfrog.com/confluence/display/RTF/User+Plugins#UserPlugins-DeployingPlugins
   * Note that for Artifactory HA, the installation directory is ${CLUSTER_HOME}/ha-etc/plugins
2. The Artifactory log directory should show the plugin being loaded:
```
2016-01-28 23:00:08,029 [art-init] [INFO ] (o.a.a.p.GroovyRunnerImpl:268) - Loading script from 'deleteStats.groovy'.
```

### Usage
The plugin will set a cron job that will cause it to be executed everyday at 11:59pm. Additionally, it will have a REST entry point to execute it on demand. The entry point is:
```
{ARTIFACTORY_URL}/api/plugins/execute/deleteStats
```

The expected output is:
```
{
    "success": true,
    "deleted": 5
}
```

The cron job will log its results in the Artifactory log but they will not be visible unless info level logging is enabled for the plugin, see https://www.jfrog.com/confluence/display/RTF/User+Plugins#UserPlugins-ControllingPluginLogLevel.

