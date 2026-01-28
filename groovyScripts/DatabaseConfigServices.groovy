package org.apache.ofbiz.quickstart

import org.apache.ofbiz.base.util.UtilXml
import org.apache.ofbiz.service.ServiceUtil
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.nio.file.*
import java.text.SimpleDateFormat
import javax.xml.transform.*
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

def getDatabaseConfigData() {
    Map results = ServiceUtil.returnSuccess()
    String engineXmlPath = System.getProperty("ofbiz.home") + "/framework/entity/config/entityengine.xml"
    
    try {
        Document doc = UtilXml.readXmlDocument(new File(engineXmlPath).toURI().toURL(), false)
        List datasources = []
        Set fieldTypes = [] as Set

        List dsElements = UtilXml.childElementList(doc.getDocumentElement(), "datasource")
        dsElements.each { ds ->
            // RÉCUPÉRATION DE L'ÉLÉMENT ENFANT JDBC
            Element inlineJdbc = UtilXml.firstChildElement(ds, "inline-jdbc")
            
            def rawUri = inlineJdbc ? inlineJdbc.getAttribute("jdbc-uri") : ds.getAttribute("jdbc-uri")
            datasources << [
                name: ds.getAttribute("name"),
                fieldType: ds.getAttribute("field-type-name"),
                // On cherche dans inline-jdbc, sinon on prend l'attribut du parent (fallback)
                uri: rawUri ? rawUri.replace("&#x3b;", ";") : "",
                username: inlineJdbc ? inlineJdbc.getAttribute("jdbc-username") : ds.getAttribute("jdbc-username"),
                driver: inlineJdbc ? inlineJdbc.getAttribute("jdbc-driver") : ds.getAttribute("jdbc-driver")
            ]
            fieldTypes << ds.getAttribute("field-type-name")
        }

        results.datasources = datasources
        results.fieldTypes = fieldTypes.toList()
    } catch (Exception e) {
        return ServiceUtil.returnError("Erreur lecture XML: " + e.message)
    }
    return results
}

def updateEntityEngineXml() {
    String dsName = context.datasourceName
    String engineXmlPath = System.getProperty("ofbiz.home") + "/framework/entity/config/entityengine.xml"
    File xmlFile = new File(engineXmlPath)

    try {
        // 1. Sauvegarde horodatée
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
        Files.copy(xmlFile.toPath(), Paths.get(engineXmlPath + "." + ts + ".bak"))

        // 2. Modification DOM
        Document doc = UtilXml.readXmlDocument(xmlFile.toURI().toURL(), false)
        Element root = doc.getDocumentElement()
        List dsElements = UtilXml.childElementList(root, "datasource")
        
        Element targetDs = dsElements.find { it.getAttribute("name") == dsName }
        targetDs.setAttribute("field-type-name", context.fieldType)
        
        Element inlineJdbc = UtilXml.firstChildElement(targetDs, "inline-jdbc")
        if (!inlineJdbc) {
            inlineJdbc = doc.createElement("inline-jdbc")
            targetDs.appendChild(inlineJdbc)
        }
        inlineJdbc.setAttribute("jdbc-uri", context.jdbcUri)
        inlineJdbc.setAttribute("jdbc-username", context.jdbcUsername)
        inlineJdbc.setAttribute("jdbc-password", context.jdbcPassword)
        inlineJdbc.setAttribute("jdbc-driver", context.jdbcDriver)

        // 3. Écriture propre via les utilitaires OFBiz
        OutputStream os = new FileOutputStream(xmlFile)
        try {
            UtilXml.writeXmlDocument(os, doc, "UTF-8", true, true)
        } finally {
            os.close()
        }

    } catch (Exception e) {
        return ServiceUtil.returnError("Erreur écriture: " + e.message)
    }
    return ServiceUtil.returnSuccess("Configuration mise à jour (Backup créé)")
}

def restartOfbiz() {
    try {
        String ofbizHome = System.getProperty("ofbiz.home")
        String scriptPath = ofbizHome + "/restart-ofbiz.sh"
        // Exécution du script shell externe
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", scriptPath)
        pb.start()
        return ServiceUtil.returnSuccess("Signal de redémarrage envoyé.")
    } catch (Exception e) {
        return ServiceUtil.returnError("Erreur redémarrage: " + e.message)
    }
}

