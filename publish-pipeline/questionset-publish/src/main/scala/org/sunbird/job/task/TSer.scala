package org.sunbird.job.task

import org.sunbird.job.util.ScalaJsonUtil

object TSer {

	def main(args: Array[String]): Unit = {
		val ll = Map("test"->"abc")
		println(ScalaJsonUtil.serialize(ll))
	}
}
