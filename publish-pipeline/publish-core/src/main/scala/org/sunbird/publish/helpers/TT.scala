package org.sunbird.publish.helpers

import java.io.File

object TT {

	def main(args: Array[String]): Unit = {
		val tt:List[Map[AnyRef, String]] = List(Map("http://test.com/icon.jpeg" -> "do_234/icon.jpeg"), Map())
		println(tt)
		val filtered = tt.filterNot(x => x.isEmpty)
		println(filtered)
		println(tt.flatten.toMap)
		val a = Map(1 -> "one", 2 -> "two", 3 -> "three")
		val b = Map(1 -> "un", 2 -> "deux", 3 -> "trois")

		val c = a.toList ++ b.toList
		println("c ::: "+c)
		val d = c.groupBy(_._1).map{case(k, v) => k -> v.map(_._2).toList}
		println(d)

		println()
	}
}
