// plugins/quickstart/groovyScripts/CreateSuperUser.groovy
package org.apache.ofbiz.quickstart

import org.apache.ofbiz.service.ServiceUtil
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.base.crypto.HashCrypt
import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.base.util.UtilProperties

def createSuperUser() {
    def module = "CreateSuperUser.groovy"
    
    try {
        String userLoginId = context.userLoginId
        String password = context.password
        String firstName = context.firstName ?: "Super"
        String lastName = context.lastName ?: "User"
        String emailAddress = context.emailAddress
        
        // Validation
        if (!userLoginId || userLoginId.length() < 3) {
            return ServiceUtil.returnError("Le login doit contenir au moins 3 caractères")
        }
        if (!password || password.length() < 6) {
            return ServiceUtil.returnError("Le mot de passe doit contenir au moins 6 caractères")
        }
        
        // Vérifier si l'utilisateur existe déjà
        def existingUser = delegator.findOne("UserLogin", [userLoginId: userLoginId], false)
        if (existingUser) {
            return ServiceUtil.returnError("Le login '${userLoginId}' existe déjà")
        }
        
        // 1. Créer le UserLogin avec mot de passe haché
        // Utiliser la méthode simplifiée de HashCrypt
        String hashedPassword = HashCrypt.cryptUTF8("PBKDF2", null, password)
        
        GenericValue userLogin = delegator.makeValue("UserLogin", [
            userLoginId: userLoginId,
            currentPassword: hashedPassword,
            passwordHint: "",
            enabled: "Y",
            requirePasswordChange: "N",
            disabledDateTime: null
        ])
        userLogin.create()
        
        // 2. Créer la Person
        String partyId = delegator.getNextSeqId("Party")
        
        GenericValue party = delegator.makeValue("Party", [
            partyId: partyId,
            partyTypeId: "PERSON",
            statusId: "PARTY_ENABLED"
        ])
        party.create()
        
        GenericValue person = delegator.makeValue("Person", [
            partyId: partyId,
            firstName: firstName,
            lastName: lastName
        ])
        person.create()
        
        // 3. Lier UserLogin à la Person
        GenericValue existingUserLogin = delegator.findOne("UserLogin", [userLoginId: userLoginId], false)
        existingUserLogin.partyId = partyId
        existingUserLogin.store()
        
        // 4. Ajouter l'email si fourni
        if (emailAddress) {
            String contactMechId = delegator.getNextSeqId("ContactMech")
            
            GenericValue contactMech = delegator.makeValue("ContactMech", [
                contactMechId: contactMechId,
                contactMechTypeId: "EMAIL_ADDRESS",
                infoString: emailAddress
            ])
            contactMech.create()
            
            GenericValue partyContactMech = delegator.makeValue("PartyContactMech", [
                partyId: partyId,
                contactMechId: contactMechId,
                fromDate: UtilDateTime.nowTimestamp(),
                allowSolicitation: "Y"
            ])
            partyContactMech.create()
            
            GenericValue partyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", [
                partyId: partyId,
                contactMechId: contactMechId,
                contactMechPurposeTypeId: "PRIMARY_EMAIL",
                fromDate: UtilDateTime.nowTimestamp()
            ])
            partyContactMechPurpose.create()
        }
        
        // 5. Assigner au groupe SUPER (super-utilisateur)
        GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup", [
            userLoginId: userLoginId,
            groupId: "SUPER",
            fromDate: UtilDateTime.nowTimestamp()
        ])
        userLoginSecurityGroup.create()
        
        // 6. Assigner aussi à FULLADMIN pour accès complet
        GenericValue userLoginSecurityGroupFullAdmin = delegator.makeValue("UserLoginSecurityGroup", [
            userLoginId: userLoginId,
            groupId: "FULLADMIN",
            fromDate: UtilDateTime.nowTimestamp()
        ])
        userLoginSecurityGroupFullAdmin.create()
        
        return ServiceUtil.returnSuccess("Superutilisateur '${userLoginId}' créé avec succès! Vous pouvez maintenant vous connecter.")
        
    } catch (Exception e) {
        e.printStackTrace()
        return ServiceUtil.returnError("Erreur lors de la création: " + e.message)
    }
}
