package shareApp.controller

import spark.*
import shareApp.model.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.ResponseEntity.status

class TaskController {

    fun getTask(): Route = Route { req, res ->
        val task = getTask(req.queryParams("id").toInt())
        task?.let { res.status(200) } ?: res.status(404)
        task
    }

    fun getTaskListByGroupId(): Route = Route { req, res ->
        val taskList= getTaskListByGroupId(req.queryParams("id").toInt())
        taskList?.let { res.status(200) } ?: res.status(404)
        taskList
    }

    fun getTaskListByUserId(): Route = Route { req, res ->
        val taskList = getTaskListByUserId(req.queryParams("id").toInt())
        taskList?.let { res.status(200) } ?: res.status(404)
        taskList
    }

    fun addTask(): Route = Route { req, res ->
        val task= addTask(jacksonObjectMapper().readValue(req.body()),findUserIdByToken(req.headers("token")))
        task?.let { res.status(200) } ?: res.status(400)
        task
    }

    fun getAllTask(): Route = Route { _, res ->
        val taskList = getAllTasks()
        taskList?.let { res.status(200) } ?: res.status(404)
        taskList
    }

    fun deleteTask(): Route = Route { req, res ->
        val result = deleteTask(findUserIdByToken(req.headers("token")),req.queryParams("id").toInt())
        if (result) res.status(204) else res.status(400)
        result
    }

}
