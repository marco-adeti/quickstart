<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3>Configuration de la Base de Données</h3>
    </div>
    <div class="screenlet-body">
        <#if configJson??>
            <h4>Configuration JSON :</h4>
            <pre style="background-color: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto;">
${configJson}</pre>
            
            <div style="margin-top: 20px;">
                <a href="<@ofbizUrl>dbConfigJson</@ofbizUrl>" class="buttontext" target="_blank">
                    Voir JSON brut
                </a>
            </div>
        <#else>
            <p>Aucune configuration disponible</p>
        </#if>
    </div>
</div>
