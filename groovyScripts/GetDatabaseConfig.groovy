import org.apache.ofbiz.entity.config.model.EntityConfig
import org.apache.ofbiz.base.util.Debug
import groovy.json.JsonBuilder

def getDatabaseConfig() {
    def module = "GetDatabaseConfig.groovy"
    def configMap = [:]
    
    try {
        // 1. Détection automatique de la datasource active via le delegator
        def helperInfo = delegator.getGroupHelperInfo("org.apache.ofbiz")
        def defaultDatasourceName = helperInfo.getHelperBaseName()
        def datasourceName = parameters.datasourceName ?: defaultDatasourceName
        
        def entityConfig = EntityConfig.getInstance()
        def datasource = entityConfig.getDatasource(datasourceName)

        if (datasource) {
            configMap.datasourceName = datasourceName
            // Utilisation de la propriété 'name' ou 'helperClass' car getHelperName() n'existe plus
            configMap.helperClass = datasource.getHelperClass()
            configMap.fieldTypeName = datasource.getFieldTypeName()
            configMap.schemaName = datasource.getSchemaName()
            
            def inlineJdbc = datasource.getInlineJdbc()
            if (inlineJdbc) {
                configMap.jdbcConnection = [
                    driver: inlineJdbc.getJdbcDriver(),
                    uri: maskPassword(inlineJdbc.getJdbcUri()),
                    username: inlineJdbc.getJdbcUsername(),
                    password: "****"
                ]
            }
        } else {
            configMap.error = "Datasource '${datasourceName}' non trouvée."
            configMap.availableDatasources = entityConfig.getDatasourceMap().keySet()
        }
    } catch (Exception e) {
        Debug.logError(e, module)
        configMap.error = "Erreur de détection : " + e.getMessage()
    }

    def jsonBuilder = new JsonBuilder(configMap)
    return [
        configJson: jsonBuilder.toPrettyString(),
        configMap: configMap
    ]
}

def maskPassword(String uri) {
    return uri ? uri.replaceAll(/password=[^;&]*/, "password=****") : ""
}

// Exécution de la logique
Map result = getDatabaseConfig()

// Si nous sommes dans un contexte de rendu d'écran (UI)
if (context != null) {
    // Injection impérative dans le contexte pour l'affichage FTL
    context.putAll(result)
}

// Pour le moteur de service (JSON), on retourne obligatoirement la Map des résultats
return result

