package shareApp.controller

import spark.*
import shareApp.model.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class GroupController {

    fun addGroup(): Route = Route { req, _ ->
        addGroup(jacksonObjectMapper().readValue(req.body()))
    }
}