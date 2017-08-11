import com.alibaba.fastjson.JSON

case class person(name: String, age: Int)

object json {
    def main(args: Array[String]): Unit = {
        val jstr = """{"name":"sb","age":30}"""
        val j = JSON.parseObject(jstr)

        val jname = j.getString("name")
        val jage = j.getInteger("age")
        val obj = person(name = jname, age = jage)

        println("name=" + obj.name)
        println("age=" + obj.age)
    }
}
