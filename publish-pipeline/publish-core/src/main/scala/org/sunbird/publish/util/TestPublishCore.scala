package org.sunbird.publish.util

import org.sunbird.job.util.ScalaJsonUtil

object TestPublishCore {

	def main(args: Array[String]): Unit = {
		val data: List[Map[String, AnyRef]] = List(Map("test"->"test_data", "hi"->List("hi", "hi"), "hello"->"hello"))
		println(ScalaJsonUtil.serialize(data))
	}
}
