package shareApp.model

import org.jetbrains.exposed.sql.*

object ContentTable : Table("contents") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 50)
    val url = varchar("url", 100)
}
