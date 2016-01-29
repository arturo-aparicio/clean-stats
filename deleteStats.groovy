/**
 * Cleans the stats table on a schedule and provides an entry point to clean it on demand
 * 
 * This REST execute is called 'deleteStats'
 * It can be called via: {ARTIFACTORY_URL}/api/plugins/execute/deleteStats
 * 
 * NOTE: This is meant to be used only for databases whose 'stat' table 'node_id' has had
 * its foreign key constraint dropped.
 * @author Arturo Aparicio
 */
import org.artifactory.storage.db.DbService
import org.artifactory.storage.db.util.JdbcHelper
import groovy.json.JsonBuilder
 
// REST Entry Point
executions {
    deleteStats(version:'0.1',
                   description:'Cleans the stats table',
                   httpMethod: 'POST') {
        def numDeleted = _deleteStats()
        def json = new JsonBuilder()
        json {
            success(true)
            deleted(numDeleted)
        }       
        message = json.toPrettyString()
    }
}

// Cron Job
jobs {
    // Run at 11:59PM every day
    buildCleanup(cron: "0 59 23 1/1 * ? *") {
        _deleteStats()
    }
}

// Cleanup method
private def _deleteStats() {
    def numDeleted = ctx.beanForType(JdbcHelper.class).executeUpdate("DELETE stats FROM stats LEFT JOIN nodes ON (stats.node_id=nodes.node_id) WHERE nodes.node_id IS NULL");
    log.info("Stats Cleanup: Removed " + numDeleted + " entries from the stats table.");
    return numDeleted;
}