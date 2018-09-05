package shareApp.controller

import spark.*
import shareApp.model.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class TaskController {

    fun getTask(): Route = Route { req, _ ->
        getTask(req.queryParams("id").toInt())
    }

    fun getTaskListByGroupId(): Route = Route { req, _ ->
        getTaskListByGroupId(req.queryParams("id").toInt())
    }

    fun getTaskListByUserId(): Route = Route { req, _ ->
        getTaskListByUserId(req.queryParams("id").toInt())
    }

    fun addTask(): Route = Route { req, _ ->
        addTask(jacksonObjectMapper().readValue(req.body()))
    }

    fun getAllTask(): Route = Route { _, _ ->
        getAllTasks()
    }
}
