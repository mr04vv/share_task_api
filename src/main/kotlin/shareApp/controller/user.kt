package shareApp.controller

import shareApp.model.*
import spark.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class UserController {


    fun getUser(): Route = Route { req, res ->
        val user = getUser(req.queryParams("id").toInt())
        user?.let { res.status(200) } ?: res.status(404)
        user
    }

    fun getUserMe(): Route = Route { req, res ->
        val user= getUser(findUserIdByToken(req.headers("token")))
        user?.let { res.status(200) } ?: res.status(404)
        user
    }

    fun getUserList(): Route = Route { req, res ->
        val userList = getUserList(req.params("id").toInt())
        userList?.let { res.status(200) } ?: res.status(404)
        userList
    }

    fun addUser(): Route = Route { req, res ->
        val user = addUser(jacksonObjectMapper().readValue(req.body()))
        user?.let { res.status(204) } ?: res.status(400)
        user
    }

    fun login(): Route = Route { req, res ->
        val user = login(jacksonObjectMapper().readValue(req.body()))
        user?.let { res.status(200) } ?: res.status(404)
        user
    }

    fun getGroups(): Route = Route { req, res ->
        val group = getGroups(findUserIdByToken(req.headers("token")))
        group?.let { res.status(200) } ?: res.status(404)
        group
    }

}
