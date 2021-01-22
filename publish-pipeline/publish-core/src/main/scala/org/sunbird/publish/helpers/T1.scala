package org.sunbird.publish.helpers

import java.io.File
import java.net.URL
import java.util

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.sunbird.publish.core.Slug

import scala.collection.JavaConverters._



// TODO: Don't modify any code in this file.  - 22 jan 2021
object T1 {

	private val onlineMimeTypes = List("video/youtube", "video/x-youtube", "text/x-url")
	private val excludeBundleMeta = List("screenshots", "posterImage")
	private val bundleLocation: String = "/tmp"
	private val defaultManifestVersion = "1.2"
	private val cloudBundleFolder = "ecar_files"
	private val manifestFileName = "manifest.json"
	private val hierarchyFileName = "hierarchy.json"
	private val hierarchyVersion = "1.0"

	//tested and working as expected
	def getRelativePath(identifier: String, value: String): String = {
		val fileName = FilenameUtils.getName(value)
		val suffix = if (fileName.endsWith(".ecar")) identifier.trim + ".zip" else Slug.makeSlug(fileName, true)
		val filePath = identifier.trim + File.separator + suffix
		filePath
	}

	def validUrl(str: String): Boolean = {
		if (str.isEmpty) false else {
			try {
				new URL(str).toURI
				true
			} catch {
				case _ => false
			}
		}
	}

	def isOnline(mimeType: String, contentDisposition: String) = onlineMimeTypes.contains(mimeType) || StringUtils.equalsIgnoreCase(contentDisposition, "online-only")

	def main(args: Array[String]): Unit = {
		/*println(getRelativePath("do_123", "http://test.com/abc.pdf"))
		println(getRelativePath("do_123", "http://test.com/pqr.ecar"))
		println(getRelativePath("do_123", "test.mp4"))*/

		// non online obj
		val map1 = Map[String, AnyRef]("identifier"->"do_123", "mimeType"->"video/mp4", "artifactUrl"->"http://abc.com/test.mp4", "contentDisposition"->"inline", "appIcon"->"http://test.com/icon2.jpeg")
		//online obj
		val map2 = Map[String, AnyRef]("identifier"->"do_234", "mimeType"->"video/youtube", "artifactUrl"->"https://www.youtube.com/watch?v=fbg_mo_eYm8&ab_channel=SonySAB", "contentDisposition"->"online-only", "appIcon"->"http://test.com/icon.jpeg")
		// non online map having a media which is already used in map1
		val map3 = Map[String, AnyRef]("identifier"->"do_345", "mimeType"->"video/mp4", "artifactUrl"->"http://abc.com/test345.mp4", "contentDisposition"->"inline", "appIcon"->"http://test.com/icon2.jpeg")
		val objList = List(map1, map2, map3)
		println("main ::: objList before proessing ::: "+objList)
		val pkgType = "FULL"
		val rootId = "do_111111"
		val rootObjType = "QuestionSet"
		val urlFields = if (StringUtils.equals("ONLINE", pkgType)) List() else List("appIcon", "grayScaleAppIcon", "artifactUrl", "itemSetPreviewUrl")

		//val downloadUrls: Map[AnyRef, List[String]] = Map()

		val (newObjList, downloadUrls) = getManifestData(rootId, rootObjType, pkgType, objList)
		println("main ::: objList :::: "+newObjList)
		println("main ::: downloadUrls :::: "+downloadUrls)
		val d = downloadUrls.flatten.groupBy(_._1).map{case(k, v) => k -> v.map(_._2)}
		println("d:::: "+d)
	}

	def getManifestData(identifier: String, objType: String, pkgType: String, objList: List[Map[String, AnyRef]]): (List[Map[String, AnyRef]], List[Map[AnyRef, String]]) = {
		objList.map(data => {
			val identifier = data.getOrElse("identifier", "").asInstanceOf[String]
			val mimeType = data.getOrElse("mimeType", "").asInstanceOf[String]
			val contentDisposition = data.getOrElse("contentDisposition", "").asInstanceOf[String]

			// get the download url and its relative path from data map
			val downloadUrl: Map[AnyRef, String] = getDownloadUrls(identifier, pkgType, isOnline(mimeType, contentDisposition), data)
			println("downloadUrl map ::: " + downloadUrl)
			// update relative path
			val updatedData: Map[String, AnyRef] = data.map(entry => if (downloadUrl.contains(entry._2)) (entry._1.asInstanceOf[String], downloadUrl.getOrElse(entry._2.asInstanceOf[String], "").asInstanceOf[AnyRef]) else entry)
			println("updated  map with relative path ::: " + updatedData)
			//set downloadUrl for each data
			val dMap = if (StringUtils.equalsIgnoreCase(contentDisposition, "online-only")) Map("downloadUrl" -> "")
			else Map("downloadUrl" -> updatedData.getOrElse("artifactUrl", "").asInstanceOf[String])
			val finalDataMap = updatedData ++ dMap
			println("final data map after updating downloadUrl ::: " + finalDataMap)

			//return the updated object map

			val ddUrls: Map[AnyRef, String] = downloadUrl.keys.flatMap(key => Map(key -> identifier)).toMap
			//downloadUrls ++ ddUrls
			println("final download url map ::: " + ddUrls)
			(finalDataMap, ddUrls)
		}).unzip
	}



	def getDownloadUrls(identifier: String, pkgType: String, isOnlineObj: Boolean, data: Map[String, AnyRef]): Map[AnyRef, String] = {
		val urlFields = if (StringUtils.equals("ONLINE", pkgType)) List() else List("appIcon", "grayScaleAppIcon", "artifactUrl", "itemSetPreviewUrl")
		data.filter(en => urlFields.contains(en._1) && null != en._2).flatMap(entry => {
			isOnlineObj match {
				case true => {
					if (!StringUtils.equalsIgnoreCase("artifactUrl", entry._1) && validUrl(entry._2.asInstanceOf[String])) {
						//Map[AnyRef, String](entry._2 -> getRelativePath(identifier, entry._2.asInstanceOf[String]))
						getUrlMap(identifier, pkgType, entry._1, entry._2)
					} else Map[AnyRef, String]()
				}
				case false => {
					if (entry._2.isInstanceOf[File]) {
						//Map[AnyRef, String](entry._2 -> getRelativePath(identifier, entry._2.asInstanceOf[File].getName))
						getUrlMap(identifier, pkgType, entry._1, entry._2)
					} else if (entry._2.isInstanceOf[String] && validUrl(entry._2.asInstanceOf[String])) {
						//Map[AnyRef, String](entry._2 -> getRelativePath(identifier, entry._2.asInstanceOf[String]))
						getUrlMap(identifier, pkgType, entry._1, entry._2)
					} else Map[AnyRef, String]()
				}
			}
		})
	}

	def getUrlMap(identifier:String, pkgType: String, key: String, value: AnyRef): Map[AnyRef, String] = {
		val pkgKeys = List("artifactUrl", "downloadUrl")
		if (!pkgKeys.contains(key) || StringUtils.equalsIgnoreCase("FULL", pkgType)) {
			val fileName = if(value.isInstanceOf[File]) value.asInstanceOf[File].getName else value.asInstanceOf[String]
			Map[AnyRef, String](value -> getRelativePath(identifier, fileName.asInstanceOf[String]))
			//val ids: List[String] = downloadUrls.getOrElse(value, List())
			//if (ids.isEmpty) downloadUrls ++ Map(value -> List(key)) else (downloadUrls ++ Map(value -> (ids ::: List(identifier))))
		} else Map[AnyRef, String]()
	}
}
