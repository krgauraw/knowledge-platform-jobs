package org.sunbird.job.task

import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import org.imgscalr.Scalr
import javax.imageio.ImageIO
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.sunbird.publish.core.{ExtDataConfig, ObjectData}

object TestH {

	private val readerConfig = ExtDataConfig("", "")
	private val THUMBNAIL_SIZE = 56
	def main(args: Array[String]): Unit = {
		val identifier = "do_123"
		val metadata = Map("appIcon"-> "https://ntpproductionall.blob.core.windows.net/ntp-content-production/content/do_31267903656791244812870/artifact/10th-ps-bit-logo_1547733956386.png", "IL_UNIQUE_ID"->"do_123", "identifier"-> "do_123", "name"->"name of do_123", "objectType"-> "QuestionSet", "visibility"-> "Default", "status"->"Draft")
		val extData = Some(Map("identifier"-> "do_123", "children"-> List(Map())))
		val hierarchy = Some(Map("identifier"-> "do_123", "children"-> List(Map("identifier"-> "do_12345", "objectType"-> "QuestionSet", "visibility"-> "Parent", "index"->1, "depth"->1, "parent"->"do_123", "children"-> List(Map("identifier"-> "do_12347", "objectType"-> "Question", "visibility"-> "Parent", "index"->1, "depth"->2, "parent"->"do_12345"))), Map("identifier"-> "do_12346", "objectType"-> "QuestionSet", "visibility"-> "Default", "index"->2, "depth"->1, "parent"->"do_123"))))
		val obj =  new ObjectData(identifier, metadata, hierarchy= hierarchy)
		println(obj)
		//val objectData = createThumbnail(obj)
		//println(objectData.toString)
		val newObj = enrichObjectMetadata(obj)
		println(newObj.toString)
	}
	def enrichObjectMetadata(obj: ObjectData): Option[ObjectData] = {
		val newMetadata: Map[String, AnyRef] = obj.metadata ++ Map("pkgVersion" -> obj.pkgVersion.asInstanceOf[AnyRef], "lastPublishedOn" -> getTimeStamp,
			"publishError" -> null, "variants" -> null, "compatibilityLevel" -> 5.asInstanceOf[AnyRef], "status" -> "Live")
		val children: List[Map[String, AnyRef]] = obj.hierarchy.getOrElse(Map()).getOrElse("children", List(Map())).asInstanceOf[List[Map[String, AnyRef]]]
		Some(new ObjectData(obj.identifier, newMetadata,hierarchy= Some(Map("identifier" -> obj.identifier, "children" -> enrichChildren(children)))))
	}
	def enrichChildren(children: List[Map[String, AnyRef]]): List[Map[String, AnyRef]] ={
		val newChildren = children.map(element => enrichMetadata(element))
		newChildren
	}
	def enrichMetadata(element: Map[String, AnyRef]): Map[String, AnyRef] = {
		println("element: *" + element.getOrElse("objectType", "").asInstanceOf[String] + "*")
		if(StringUtils.equalsIgnoreCase(element.getOrElse("objectType", "").asInstanceOf[String], "QuestionSet")
		  && StringUtils.equalsIgnoreCase(element.getOrElse("visibility", "").asInstanceOf[String], "Parent")){
			val children : List[Map[String, AnyRef]]= element.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]]
			val enrichedChildren = enrichChildren(children)
			element ++ Map("children" -> enrichedChildren, "status" -> "Live")
		} else if (StringUtils.equalsIgnoreCase(element.getOrElse("objectType", "").toString, "QuestionSet")
		  && StringUtils.equalsIgnoreCase(element.getOrElse("visibility", "").toString, "Default")){
			val childHierarchy: Map[String, AnyRef] = getHierarchy(element.getOrElse("identifier", "").toString, readerConfig).getOrElse(Map())
			childHierarchy ++ Map("index" -> element.getOrElse("index", 0).asInstanceOf[AnyRef], "depth" -> element.getOrElse("depth", 0).asInstanceOf[AnyRef], "parent" -> element.getOrElse("parent", ""))
		} else if(StringUtils.equalsIgnoreCase(element.getOrElse("objectType", "").toString, "Question")){
			val newObject: ObjectData = getObject(element.getOrElse("identifier", "").toString, 0.asInstanceOf[Double], readerConfig)
			newObject.metadata ++ Map("index" -> element.getOrElse("index", 0).asInstanceOf[AnyRef], "parent" -> element.getOrElse("parent", ""), "depth" -> element.getOrElse("depth", 0).asInstanceOf[AnyRef])
		}else Map()
	}
	def getTimeStamp(): String = {
		val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.format(new Date())
	}
	def getHierarchy(identifier: String, readerConfig: ExtDataConfig): Option[Map[String, AnyRef]] = {
		Some(Map("identifier"-> "do_12346", "objectType"-> "QuestionSet", "visibility"-> "Default", "name"-> "name of do_12346", "children"-> List(Map("identifier"-> "do_12346111", "name"->"name of do_12346111", "objectType"-> "Question", "visibility"->"Parent"))))
	}
	def getObject(identifier: String, pkgVersion: Double, readerConfig: ExtDataConfig): ObjectData = {
		val identifier: String = "do_12347"
		val metadata : Map[String, AnyRef] = Map("IL_UNIQUE_ID" -> "do_12347", "name"->"name of do_12347", "objectType"-> "Question")
		//val extData = Some(Map("identifier"-> "do_123", "children"-> List(Map())))
		val hierarchy = Some(Map("identifier"-> "do_12347", "children"-> List(Map())))
		new ObjectData(identifier, metadata, hierarchy=hierarchy)
	}
	def createThumbnail(objectData: ObjectData): Option[ObjectData] ={
		val appIcon: String = objectData.metadata.getOrElse("appIcon", "").asInstanceOf[String]
		val downloadedAppIconFile : File = new File("") // implement downloadFile method
		if(downloadedAppIconFile.exists() && downloadedAppIconFile.isFile){
			val generated: Boolean = generate(downloadedAppIconFile)
			if(generated){
				val thumbnail: String = downloadedAppIconFile.getParent + File.separator + FilenameUtils.getBaseName(downloadedAppIconFile.getPath) + ".thumb." + FilenameUtils.getExtension(downloadedAppIconFile.getPath)
				val generatedThumbnailFile: File = new File(thumbnail)
				if(generatedThumbnailFile.exists()){
					// Upload file to cloid store and get url Array
					// set metadata appIcon = thumUrl and posterImage as old appIcon
					new ObjectData(objectData.identifier, objectData.metadata ++ Map("appIcon"-> "thumUrl", "posterImage" -> appIcon))
				}
			}
		}
		Some(new ObjectData(objectData.identifier, Map()))
	}
	def generate(inFile: File): Boolean = {
		try {
			val thumbFile = getThumbnailFileName(inFile)
			val outFile = new File(thumbFile)
			val success = generate(inFile, outFile)
			success
		} catch {
			case ex: Exception =>
				false
		}
	}
	def getThumbnailFileName(input: File): String = {
		val outputFileName = input.getName.replaceAll("\\.", "\\.thumb\\.")
		val outputFolder = input.getParent
		outputFolder + "/" + outputFileName
	}
	def generate(inFile: File, outFile: File): Boolean = {
		var done = false
		if ((inFile == null) || (outFile == null))
			done = false
		else try {
			val srcImage = ImageIO.read(inFile)
			val width = srcImage.getWidth
			val height = srcImage.getHeight
			if ((height > THUMBNAIL_SIZE) || (width > THUMBNAIL_SIZE)) {
				val scaledImage: BufferedImage = Scalr.resize(srcImage, THUMBNAIL_SIZE) // Scale image
				ImageIO.write(scaledImage, "png", outFile)
				done = true
			}else{
				ImageIO.write(srcImage, "png", outFile)
				done = false
			}
		} catch{
			case ex: Exception =>
				done = false
		}
		done
	}
}
