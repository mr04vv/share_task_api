package shareApp.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import shareApp.model.*

fun dbConnect() {
    Database.connect("jdbc:mysql://localhost:3306/share_app", "com.mysql.jdbc.Driver", "root", "root")
    transaction {
        create(UserTable)
        create(TaskTable)
        create(ContentTable)
        create(CommentTable)
        create(TokenTable)
        create(GroupMemberTable)

        create(GroupTable)

    }
}

fun dbConnectHeroku() {
    Database.connect("jdbc:postgresql://ec2-184-73-175-95.compute-1.amazonaws.com:5432/d6is229dd8d5he?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "com.mysql.jdbc.Driver","gdotzlmesfdrwv","aca5abbf7b12ecbedb441df86f58c254e02373acc1549737918d45a46f230b04")
    transaction {
        create(UserTable)
        create(TaskTable)
        create(ContentTable)
        create(CommentTable)
        create(GroupTable)
        create(GroupMemberTable)
        create(TokenTable)
    }
}

fun dbConnectGcp() {
    Database.connect("jdbc:google:mysql://server-202216:us-central1:linebotsql/share?user=root&amp;password=root", "com.mysql.jdbc.GoogleDriver")
    transaction {
        create(UserTable)
        create(TaskTable)
        create(ContentTable)
        create(CommentTable)
        create(GroupTable)
        create(GroupMemberTable)
        create(TokenTable)
    }
}