package org.sunbird.publish.helpers

import org.sunbird.publish.core.ObjectData

class CustomFn {


/*
	def enrichForBundle(obj:ObjectData): List[Map[String, AnyRef]] = {
		// remove "posterimage" and "appicon"
		//val updatedList = hierarchy
		//val li = List(obj.metadata)++updatedList
		// Update All the child objects with root metadata - 		TODO: This should be out of processEcar
		// Update root object with flat children list
		// Update all the child objects which has children - with flat children list
		val rootMetadata = obj.metadata

		val children: List[Map[String, AnyRef]] = obj.hierarchy.get.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]]

		// Flat Map foor all children having children if exist
		val manifestMap: List[Map[String, AnyRef]]= children.map(child => {
			//val childMap = getChildMap(child)
			/*if(child.keySet.contains("children")) {
				child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]].map(ch => getChildMap(ch))
			}*/
		})
		List(Map("a" -> Map[String, AnyRef]))
	}

	/*def getFlat(children: List[Map[String, AnyRef]]): List[Map[String, AnyRef]] = {
		//val newChild = children
		//val ll = List[Map[String, AnyRef]]()
		children.map(child => {
			val childMap: Map[String, AnyRef] = getChildMap(child)
			//val tt = ll ++ childMap
			if(child.keySet.contains("children")) {
				val chMap:List[Map[String, AnyRef]] = getFlat(child.getOrElse("children", List()).asInstanceOf[List[Map[String, AnyRef]]])
				(chMap ::: List(childMap)).flatten
			}else childMap
		}).toMap.flatten
	}*/


	def setRootObjectChildren(objMetadata: Map[String, AnyRef], children: List[Map[String, AnyRef]]): List[Map[String, AnyRef]] = {
		val abc: List[String] = List("identifier", "name", "objectType","description","index")
		val obj: Map[String, AnyRef] = objMetadata
		val flatChildren: List[Map[String, AnyRef]] = children.map(child => child.filterKeys(key => abc.contains(key)))
		List(Map("a" -> Map[String, AnyRef]))
	}*/

}
