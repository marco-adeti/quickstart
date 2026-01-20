<div style="padding: 20px;">
    <h2>🛠 Configuration de la Base de Données</h2>
    <div class="screenlet">
        <div class="screenlet-body">
            <form method="post" action="updateDatabaseConfig" id="dbConfigForm">
                <table class="basic-table">
                    <tr>
                        <td class="label">Datasource existante</td>
                        <td>
                            <select name="datasourceName" id="dsSelector" onchange="updateFields()" style="width:300px">
                                <option value="">-- Créer une nouvelle --</option>
                                <#list dbData.datasources as ds>
                                    <option value="${ds.name}">${ds.name}</option>
                                </#list>
                            </select>
                        </td>
                    </tr>
                    <tr><td class="label">Field Type Name</td>
                        <td>
                            <select name="fieldType" id="fieldType">
                                <#list dbData.fieldTypes as ft><option value="${ft}">${ft}</option></#list>
                            </select>
                        </td>
                    </tr>
                    <tr><td class="label">JDBC URI</td><td><input type="text" name="jdbcUri" id="jdbcUri" size="80"/></td></tr>
                    <tr><td class="label">Username</td><td><input type="text" name="jdbcUsername" id="jdbcUsername"/></td></tr>
                    <tr><td class="label">Password</td><td><input type="password" name="jdbcPassword" id="jdbcPassword"/></td></tr>
                    <tr><td class="label">Driver Class</td><td><input type="text" name="jdbcDriver" id="jdbcDriver" size="50"/></td></tr>
                    <tr><td></td><td><input type="submit" value="Enregistrer la configuration" class="smallSubmit"/></td></tr>
                </table>
            </form>
            
            <hr/>
            
            <form method="post" action="restartOfbiz">
                <p style="color: #d9534f; font-weight: bold;">⚠️ Le redémarrage est nécessaire pour appliquer les changements.</p>
                <input type="submit" value="Redémarrer OFBiz" class="smallSubmit" style="background-color: #d9534f; color:white;"
                       onclick="return confirm('Le serveur va redémarrer. Continuer ?')"/>
            </form>
        </div>
    </div>
</div>

<script>
    const dsData = {
        <#list dbData.datasources as ds>
            "${ds.name}": {
                "uri": "${ds.uri?js_string}",
                "username": "${ds.username?js_string}",
                "driver": "${ds.driver?js_string}",
                "fieldType": "${ds.fieldType?js_string}"
            }<#if ds_has_next>,</#if>
        </#list>
    };

    function updateFields() {
        const sel = document.getElementById('dsSelector').value;
        const data = dsData[sel] || {uri: "", username: "", driver: "", fieldType: ""};
        
        document.getElementById('jdbcUri').value = data.uri;
        document.getElementById('jdbcUsername').value = data.username;
        document.getElementById('jdbcDriver').value = data.driver;
        document.getElementById('fieldType').value = data.fieldType;
    }
</script>

