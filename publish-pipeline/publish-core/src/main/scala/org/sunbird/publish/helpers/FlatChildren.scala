package org.sunbird.publish.helpers

import java.io.File
import java.util

import scala.collection.JavaConverters._

object FlatChildren {

	val list = new util.ArrayList[Map[String, AnyRef]]()

	def main(args: Array[String]): Unit = {
		val childList = List(Map("name"-> "QS1", "children"-> List(Map("name"-> "QS1_Q1", "test"->"test"),Map("name"-> "QS1_Q2", "test2"->"test2"))),
							 Map("name"-> "QS2", "children"-> List(Map("name"-> "QS2_QS1", "children"->List(Map("name"-> "QS2_QS1_Q1"),Map("name"-> "QS1_Q2", "test2"->"test2")))))
		)
		println("childList :: "+childList)
		getFlatStructure(childList)
		println("result :: "+list)
		println("result count :: "+list.size)
		println("result uniq :: "+list.asScala.distinct.size)
		println("result ::::: 111 ::: "+list.asScala.distinct)

	}

	def getFlatStructure(children: List[Map[String, AnyRef]]) : Unit = {
		children.map(child => {
			list.add(getChildMap(child))
			if(child.keySet.contains("children")) {
				val ch = child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]]
				getFlatStructure(ch)
			}

		})
	}

	def getChildMap(child: Map[String, AnyRef]) = {
		val metaList: List[String] = List("identifier", "name", "objectType","description","index")
		val metadata: Map[String, AnyRef] = child - ("children")
		val flatChildren: List[Map[String, AnyRef]] = child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]].map(ch => ch.filterKeys(key => metaList.contains(key)))
		val updatedMap = if(flatChildren.nonEmpty) metadata ++ Map("children" -> flatChildren) else metadata
		updatedMap
	}

}
