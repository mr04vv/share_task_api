package shareApp.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark.halt

object GroupTable : Table("groups") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50)
}

object GroupMemberTable : Table("group_members") {
    val id = integer("id").autoIncrement().primaryKey()
    val group_id = integer("group_id").references(GroupTable.id, ReferenceOption.CASCADE)
    val user_id = integer("user_id").references(UserTable.id, ReferenceOption.CASCADE)
}

data class GroupMember(
        var id: Int? = 0,
        var name: String = ""
)

data class Group(
        var id: Int? = null,
        var name: String? = null
)

fun addGroup(group: Group): Group {

    transaction {
        group.id = GroupTable.insert {
            it[GroupTable.name] = group.name
        } get GroupTable.id
    }
    if (group.id == 0) throw halt(400, "can't create group")
    return group
}