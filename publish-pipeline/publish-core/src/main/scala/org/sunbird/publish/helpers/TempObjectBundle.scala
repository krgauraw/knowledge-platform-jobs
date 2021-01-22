package org.sunbird.publish.helpers

import java.io.File
import java.util

import org.sunbird.job.util.JSONUtil
import java.util

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.sunbird.publish.core.{ObjectData, Slug}

import scala.collection.JavaConverters._

object TempObjectBundle {

	def convertJsonMetadata(objList: util.List[util.Map[String, AnyRef]], jsonProps: scala.List[String]) = {
		objList.asScala.toList.map(data => {
			data.entrySet().forEach(entry => {
				entry.setValue(convertJsonProperties(entry, jsonProps))
				entry
			})
		})
	}

	def convertJsonProperties(entry: util.Map.Entry[String, AnyRef], jsonProps: scala.List[String]) = {
		if (jsonProps.contains(entry.getKey)) {
			try {
				JSONUtil.deserialize[java.util.HashMap[String, AnyRef]](entry.getValue.asInstanceOf[String])
			} catch {
				case e: Exception => entry.getValue
			}
		} else entry.getValue
	}


	def getBundleUrl(obj: ObjectData, objList: List[Map[String, AnyRef]], fileName: String, downloadUrls: Map[AnyRef, List[String]]): Unit = {
		//val bundleFileName = bundleLocation + File.separator + fileName
		//val bundlePath = bundleLocation + File.separator + System.currentTimeMillis + "_temp"


	}

	def downloadFile(bundlePath: String, downloadUrls: Map[AnyRef, List[String]]): List[File] = {
		List()
	}


	// this method should check and add object identifier against a mediaUrl/mediaFile . e.g: Map("http://abc.com/sample.pdf"-> List("do_123", "do_234"))
	def addDownloadUrl(downloadUrls: Map[AnyRef, List[String]], identifier: String, key: String, value: AnyRef, pkgType: String): Map[AnyRef, List[String]] = {
		val pkgKeys = List("artifactUrl", "downloadUrl")
		if (!pkgKeys.contains(key) || StringUtils.equalsIgnoreCase("FULL", pkgType)) {
			val ids: List[String] = downloadUrls.getOrElse(value, List())
			if (ids.isEmpty) downloadUrls ++ Map(value -> List(key)) else (downloadUrls ++ Map(value -> (ids ::: List(identifier))))
		} else Map()
	}

	/*def getManifestData(identifier: String, objList: List[Map[String, AnyRef]], pkgType: String): Unit = {
		val urlFields = if (StringUtils.equals("ONLINE", pkgType)) List() else List("appIcon", "grayScaleAppIcon", "artifactUrl", "itemSetPreviewUrl")
		val downloadUrls: Map[AnyRef, List[String]] = Map()
		objList.map(data => {
			//val data = map.asScala
			val identifier = data.getOrElse("identifier", "").asInstanceOf[String]
			val mimeType = data.getOrElse("mimeType", "").asInstanceOf[String]
			val contentDisposition = data.getOrElse("contentDisposition", "").asInstanceOf[String]
			data.map(entry => {
				if(urlFields.contains(entry._1) && null != entry._2) {
					isOnline(mimeType, contentDisposition) match {
						case true => {
							if(!StringUtils.equalsIgnoreCase("artifactUrl", entry._1) && validUrl(entry._2.asInstanceOf[String])) {
								addDownloadUrl(downloadUrls, identifier, entry._1, entry._2 , pkgType)
								val fileName = FilenameUtils.getName(entry._2.toString)
								val value = identifier.trim + File.separator + Slug.makeSlug(fileName, true)
								// TODO: Why We need to set this to entry value
								//entry.setValue(value)
							}
						}
						case false => {
							if(entry._2.isInstanceOf[File]) {

							} else if (entry._2.isInstanceOf[String] && validUrl(entry._2.asInstanceOf[String])) {

							} else if(entry._2.isInstanceOf[util.List[String]]) {

							}
						}
					}
				}
			})
			// set download url for manifest

		})
		//val updatedObjList

	}*/



	/*// this method should check and add object identifier against a mediaUrl/mediaFile . e.g: Map("http://abc.com/sample.pdf"-> List("do_123", "do_234"))
	def addDownloadUrl(downloadUrls: Map[AnyRef, List[String]], identifier: String, key: String, value: AnyRef, pkgType: String): Map[AnyRef, List[String]] = {
		val pkgKeys = List("artifactUrl", "downloadUrl")
		if (!pkgKeys.contains(key) || StringUtils.equalsIgnoreCase("FULL", pkgType)) {
			val ids: List[String] = downloadUrls.getOrElse(value, List())
			if (ids.isEmpty) downloadUrls ++ Map(value -> List(key)) else (downloadUrls ++ Map(value -> (ids ::: List(identifier))))
		} else Map()
	}*/


}
