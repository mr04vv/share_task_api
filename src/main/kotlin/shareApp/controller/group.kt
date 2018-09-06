package shareApp.controller

import spark.*
import shareApp.model.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class GroupController {

    fun addGroup(): Route = Route { req, res ->
        val group = addGroup(jacksonObjectMapper().readValue(req.body()))
        group?.let { res.status(204) } ?: res.status(404)
        group
    }
}