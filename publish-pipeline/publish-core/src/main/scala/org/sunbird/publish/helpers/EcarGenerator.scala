package org.sunbird.publish.helpers

import java.io.File

import org.slf4j.LoggerFactory
import org.sunbird.publish.core.ObjectData

trait EcarGenerator extends ObjectBundle {

	private[this] val logger = LoggerFactory.getLogger(classOf[EcarGenerator])

	// This method returns map of pkgType and its cloud url
	def generateEcar(obj: ObjectData, pkgType: List[String], customFn: (ObjectData) => List[Map[String, AnyRef]]): Map[String, String] = {
		logger.info("Generating Ecar For : " + obj.identifier)
		// custom function will do children enrichment based on object type behaviour. generally applicable only for collections
		// in case of other than collection, customFn should return the root node enriched metadata as List[Map[String, AnyRef]]
		val enObjects: List[Map[String, AnyRef]] = customFn(obj)
		pkgType.flatMap(pkg => Map(pkg -> generateEcar(obj, enObjects, pkg))).toMap
	}

	// this method returns only cloud url for given pkg
	def generateEcar(obj: ObjectData, objList: List[Map[String, AnyRef]], pkgType: String): String = {
		logger.info(s"Generating ${pkgType} Ecar For : " + obj.identifier)
		// get bundle file (ecar file)
		val bundle: File = getObjectBundle(obj, objList, pkgType)
		// TODO: upload and return the url
		"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/ecar_files/do_113105564164997120111/1-vsa-qts-2_1603203738131_do_113105564164997120111_1.0_spine.ecar"
	}

}
