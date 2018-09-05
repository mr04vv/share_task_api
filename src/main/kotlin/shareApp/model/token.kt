package shareApp.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark.halt
import java.util.UUID

object TokenTable : Table("tokens") {
    val token = varchar("token", 100)
    val user_id = integer("user_id")
}

data class Token(
        val token: String,
        val user_id: Int
)

// tokenを生成
fun createToken(u_id: Int): String {
    val uuid = UUID.randomUUID().toString()
    val t = Token(uuid, u_id)
    insertToken(t)
    return t.token
}

// tokenを追加
fun insertToken(t: Token) {
    // userIdとtokenをdbに保存
    transaction {
        TokenTable.insert {
            it[token] = t.token
            it[user_id] = t.user_id
        }
    }
}

// tokenからuserId取得
fun findUserIdByToken(t: String): Int {
    var userId: Int = 0
    transaction {
        TokenTable.select {
            TokenTable.token.eq(t)
        }.forEach {
                    userId = it[TokenTable.user_id]
                }
    }
    if (userId == 0) throw halt(404, "is not exist")
//    ここでエラー処理 書き直す
//    TODO
    // userId
    return userId
}
