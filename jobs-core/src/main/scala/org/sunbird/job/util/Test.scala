package org.sunbird.job.util

object Test {

	def main(args: Array[String]): Unit = {
		val data: List[Map[String, AnyRef]] = List(Map("test"->"test_data", "hi"->List("hi", "hi"), "hello"->"hello"))
		println(ScalaJsonUtil.serialize(data))
	}
}
