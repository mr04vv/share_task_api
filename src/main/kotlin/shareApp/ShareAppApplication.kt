package shareApp

import shareApp.conf.Routes
import shareApp.db.*
import spark.Spark.secure

fun main(args: Array<String>) {

     dbConnect() //localDBに接続
//    dbConnectHeroku() //herokuのpostgreに接続
//    dbConnectGcp()

    filter() //cors許容
    Routes().init()
    

}
