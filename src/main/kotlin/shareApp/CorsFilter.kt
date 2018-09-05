package shareApp

import spark.Spark.*


fun filter() {
    setPort(getHerokuAssignedPort())
//cors許容
    before("*", { _, res ->
        res.header("Content-Type", "application/json")
        res.header("Access-Control-Allow-Origin", "*")
        res.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin")
        res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS")
    })
}

fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 4567
//return default port if heroku-port isn't set (i.e. on localhost)
}
