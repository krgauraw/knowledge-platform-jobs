package org.sunbird.publish.helpers

import org.apache.commons.lang3.StringUtils
import org.sunbird.job.util.ScalaJsonUtil

object FlatChildren {

	//    val list = new util.ArrayList[Map[String, AnyRef]]()
	def main(args: Array[String]): Unit = {
		val childList = List(Map("identifier" -> "QS1", "depth" -> 1.asInstanceOf[AnyRef], "index" -> 1.asInstanceOf[AnyRef], "name" -> "QS1", "children" -> List(Map("identifier" -> "QS1_Q1", "depth" -> 2.asInstanceOf[AnyRef], "index" -> 1.asInstanceOf[AnyRef], "name" -> "QS1_Q1", "test" -> "test"), Map("identifier" -> "QS1_Q2", "depth" -> 2.asInstanceOf[AnyRef], "index" -> 2.asInstanceOf[AnyRef], "name" -> "QS1_Q2", "test2" -> "test2"),Map("identifier" -> "QS2_QS1_Q1", "depth" -> 2.asInstanceOf[AnyRef], "index" -> 3.asInstanceOf[AnyRef], "name" -> "QS2_QS1_Q1"))),
			Map("identifier" -> "QS2", "depth" -> 1.asInstanceOf[AnyRef], "index" -> 2.asInstanceOf[AnyRef], "name" -> "QS2", "children" -> List(Map("identifier" -> "QS2_QS1", "depth" -> 2.asInstanceOf[AnyRef], "index" -> 1.asInstanceOf[AnyRef], "name" -> "QS2_QS1", "children" -> List(Map("identifier" -> "QS2_QS1_Q1", "depth" -> 3.asInstanceOf[AnyRef], "index" -> 1.asInstanceOf[AnyRef], "name" -> "QS2_QS1_Q1"),
				Map("identifier" -> "QS2_QS1_Q2", "depth" -> 3.asInstanceOf[AnyRef], "index" -> 2.asInstanceOf[AnyRef], "name" -> "QS2_QS1_Q2", "test2" -> "test2")))))
		)

		val updatedMetadata = Map("identifier"->"do_1234", "name"->"Test QuestionSet", "status"->"Live","children"->childList)
		val inputList = List(updatedMetadata)
		println("inputList :: " + inputList)
		val flattenedMap = getFlatStructure(inputList, List())
		println(ScalaJsonUtil.serialize(flattenedMap))
		//println(ScalaJsonUtil.serialize(flattenedMap.distinct))
	}
	def getFlatStructure(children: List[Map[String, AnyRef]], childrenList: List[Map[String, AnyRef]]): List[Map[String, AnyRef]] = {
		children.flatMap(child => {
			val innerChildren = getInnerChildren(child)
			val updatedChild: Map[String, AnyRef] = if (innerChildren.nonEmpty) child ++ Map("children" -> innerChildren) else child
			val finalChild = updatedChild.filter(p=> !List("index", "depth").contains(p._1.asInstanceOf[String]))
			val updatedChildren: List[Map[String, AnyRef]] = finalChild :: childrenList
			val result = getFlatStructure(child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]], updatedChildren)
			finalChild :: result
		}).distinct
	}
	def getInnerChildren(child: Map[String, AnyRef]): List[Map[String, AnyRef]] = {
		val metaList: List[String] = List("identifier", "name", "objectType", "description", "index")
		child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]]
		  .map(ch => ch.filterKeys(key => metaList.contains(key)))
	}

}
