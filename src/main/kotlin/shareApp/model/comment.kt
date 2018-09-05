package shareApp.model

import org.jetbrains.exposed.sql.*

object CommentTable : Table("comments") {
    val id = integer("id").autoIncrement().primaryKey()
    val task_id = integer("task_id")
    val user_id = integer("user_id")
    val body = varchar("body", 100)
    val dest_comment_id = integer("dest_id")
}
