<#-- plugins/quickstart/webapp/quickstart/config/CreateSuperUser.ftl -->
<div style="padding: 20px; max-width: 600px; margin: 0 auto;">
    <h2>👤 Créer un Superutilisateur</h2>
    
    <#if _message_??>
        <div class="alert alert-success" style="padding: 10px; margin: 10px 0; background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; border-radius: 4px;">
            ✓ ${_message_}
        </div>
    </#if>
    
    <#if _error_message_??>
        <div class="alert alert-danger" style="padding: 10px; margin: 10px 0; background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; border-radius: 4px;">
            ✗ ${_error_message_}
        </div>
    </#if>
    
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3>Informations du compte</h3>
        </div>
        <div class="screenlet-body">
            <form method="post" action="<@ofbizUrl>createSuperUser</@ofbizUrl>" id="superUserForm" onsubmit="return validateForm()">
                <table class="basic-table" cellspacing="0">
                    <tr>
                        <td class="label" style="width: 30%;"><span style="color: red;">*</span> Login :</td>
                        <td>
                            <input type="text" name="userLoginId" id="userLoginId" size="30" required 
                                   placeholder="admin" pattern="[a-zA-Z0-9_-]{3,50}" 
                                   title="3-50 caractères, lettres, chiffres, _ et - uniquement"/>
                            <br/><small style="color: #666;">Minimum 3 caractères</small>
                        </td>
                    </tr>
                    <tr>
                        <td class="label"><span style="color: red;">*</span> Mot de passe :</td>
                        <td>
                            <input type="password" name="password" id="password" size="30" required 
                                   minlength="6" placeholder="••••••••"/>
                            <br/><small style="color: #666;">Minimum 6 caractères</small>
                        </td>
                    </tr>
                    <tr>
                        <td class="label"><span style="color: red;">*</span> Confirmer mot de passe :</td>
                        <td>
                            <input type="password" name="confirmPassword" id="confirmPassword" size="30" required 
                                   minlength="6" placeholder="••••••••"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><hr style="margin: 15px 0;"/></td>
                    </tr>
                    <tr>
                        <td class="label">Prénom :</td>
                        <td>
                            <input type="text" name="firstName" size="30" placeholder="Jean"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">Nom :</td>
                        <td>
                            <input type="text" name="lastName" size="30" placeholder="Dupont"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">Email :</td>
                        <td>
                            <input type="email" name="emailAddress" size="30" placeholder="admin@example.com"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" style="padding-top: 20px;">
                            <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 10px; border-radius: 4px;">
                                <strong>⚠️ Important :</strong> Ce compte aura un accès complet à OFBiz (groupes SUPER et FULLADMIN).
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td style="padding-top: 15px;">
                            <input type="submit" value="Créer le superutilisateur" class="smallSubmit" 
                                   style="background-color: #28a745; color: white; padding: 10px 20px; font-weight: bold;"/>
                            <a href="<@ofbizUrl>main</@ofbizUrl>" class="buttontext" style="margin-left: 10px;">Annuler</a>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
    
    <div class="screenlet" style="margin-top: 20px;">
        <div class="screenlet-title-bar">
            <h3>ℹ️ Informations</h3>
        </div>
        <div class="screenlet-body">
            <h4>Droits accordés :</h4>
            <ul>
                <li><strong>SUPER</strong> : Accès administrateur complet au système</li>
                <li><strong>FULLADMIN</strong> : Tous les droits sur toutes les applications</li>
            </ul>
            
            <h4>Utilisation :</h4>
            <p>Une fois créé, vous pourrez vous connecter avec ce compte sur :</p>
            <code>https://localhost:8443/accounting/control/main</code><br/>
            <code>https://localhost:8443/webtools/control/main</code><br/>
            <small style="color: #666;">Ou toute autre application OFBiz</small>
        </div>
    </div>
</div>

<script type="text/javascript">
function validateForm() {
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        alert('Les mots de passe ne correspondent pas !');
        return false;
    }
    
    if (password.length < 6) {
        alert('Le mot de passe doit contenir au moins 6 caractères !');
        return false;
    }
    
    var userLoginId = document.getElementById('userLoginId').value;
    if (userLoginId.length < 3) {
        alert('Le login doit contenir au moins 3 caractères !');
        return false;
    }
    
    return confirm('Créer le superutilisateur "' + userLoginId + '" ?');
}
</script>
