package org.sunbird.publish.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.Gson
import org.sunbird.job.util.JSONUtil

object Test {

	@transient val mapper = new ObjectMapper()
	mapper.registerModule(DefaultScalaModule)

	@throws(classOf[Exception])
	def serialize(obj: AnyRef): String = {
		mapper.writeValueAsString(obj);
	}

	def main(args: Array[String]): Unit = {

		//val exlude = List("hi")
		val data: List[Map[String, AnyRef]] = List(Map("test"->"test_data", "hi"->List("hi", "hi"), "hello"->"hello"))
		/*val updatedData = data.map(node=> {
			node.filterKeys(key => !exlude.contains(key))
		})*/
		//println("updated data ::: "+updatedData)
		//val string = new Gson().toJson(data)
		val string = serialize(data)
		println("data :: "+string)

		/*val arr = Array("test", "test2")
		val ll = List("test","tt").asJava.toArray
		//val intArray: String[] = { "test","tt" }
		//val jarr = arr.as
		println(arr)
		println(ll.toString)*/
	}
}
