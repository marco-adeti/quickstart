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
            datasources << [
                name: ds.getAttribute("name"),
                fieldType: ds.getAttribute("field-type-name"),
                uri: ds.getAttribute("jdbc-uri"),
                username: ds.getAttribute("jdbc-username"),
                driver: ds.getAttribute("jdbc-driver")
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

        if (!targetDs) {
            targetDs = doc.createElement("datasource")
            targetDs.setAttribute("name", dsName)
            root.appendChild(targetDs)
        }

        targetDs.setAttribute("field-type-name", context.fieldType)
        targetDs.setAttribute("jdbc-uri", context.jdbcUri)
        targetDs.setAttribute("jdbc-username", context.jdbcUsername)
        targetDs.setAttribute("jdbc-password", context.jdbcPassword)
        targetDs.setAttribute("jdbc-driver", context.jdbcDriver)

        // 3. Écriture
        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        transformer.transform(new DOMSource(doc), new StreamResult(xmlFile))

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

