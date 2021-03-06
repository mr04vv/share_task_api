package shareApp.model

import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

// userTable
object UserTable : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50).uniqueIndex()
    val nick_name = varchar("nick_name", 50)
    val password = varchar("password", 100)
}

data class User(
        var id: Int = 0,
        var name: String = "",
        var nick_name: String = "",
        var password: String = ""
)

data class ResponseUserData(
        var id: Int = 0,
        var name: String = "",
        var nick_name: String = "",
        var group: MutableList<Group>? = null
)

data class ResponseUserDataWithToken(
        var id: Int = 0,
        var name: String = "",
        var nick_name: String = "",
        var group: MutableList<Group>? = null,
        var token: String? = null
)


// loginメソッド
fun login(u: User): ResponseUserDataWithToken? {

    u.password = hashString("SHA-256", u.password)

    // userテーブルからselect
    return try {
        transaction {
            UserTable.select {
                UserTable.name.eq(u.name) and UserTable.password.eq(u.password)
            }.forEach {
                u.id = it[UserTable.id]
                u.nick_name = it[UserTable.nick_name]
                /* u.group_id = it[UserTable.group_id] */
            }
        }

        // token生成
        val token = createToken(u.id)
        // userが属しているグループ参照
        val groups = getGroups(findUserIdByToken(token))

        ResponseUserDataWithToken(u.id, u.name, u.nick_name, groups, token)
    } catch (e: Exception) {
        null
    }
}

// createUserメソッド
fun addUser(u: User): ResponseUserData? {

    // passのハッシュ化
    u.password = hashString("SHA-256", u.password)

    // insert
    return try {
        transaction {
            try {
                u.id = UserTable.insert {
                    it[name] = u.name
                    it[nick_name] = u.nick_name
                    it[password] = u.password
                } get UserTable.id
            } catch (e: Exception) {
                // nameの被りで400
                throw halt(400, "this name is already exist") //大概ユーザー名被り
            }
        }
        ResponseUserData(u.id, u.name, u.nick_name, null)
    } catch (e: Exception) {
        null
    }
}

// getUserInfo
fun getUser(id: Int): ResponseUserData? {
    var group: Group
    val group_id: MutableList<Group> = mutableListOf()
    lateinit var user: User

    // userIdからuserInfo取得
    return try {
        transaction {
            UserTable.select {
                UserTable.id.eq(id)
            }.forEach {
                user = User(it[UserTable.id], it[UserTable.name], it[UserTable.nick_name]
                        , it[UserTable.password])
            }


            // getGroupじゃだめなのか？要調査 TODO
            (GroupMemberTable innerJoin GroupTable)
                    .slice(GroupMemberTable.group_id, GroupTable.name)
                    .select {
                        GroupMemberTable.user_id.eq(user.id)
                    }.forEach {
                        group = Group(it[GroupMemberTable.group_id], it[GroupTable.name])
                        group_id += group
                    }
        }

        ResponseUserData(user.id, user.name, user.nick_name, group_id)
    } catch (e: Exception) {
        null
    }
}

// userList取得
fun getUserList(group: Int): MutableList<GroupMember>? {
    var user: GroupMember
    val users: MutableList<GroupMember> = mutableListOf() // groupMemberのリスト

    // groupIdからgroup参照してuserTableにjoin→groupMemberのname,id取得
    return try {
        transaction {
            (GroupMemberTable innerJoin UserTable).slice(UserTable.id, UserTable.name, UserTable.nick_name).select {
                GroupMemberTable.group_id.eq(group)
            }.forEach {
                user = GroupMember(it[UserTable.id], it[UserTable.name], it[UserTable.nick_name])
                users += user
            }
        }
        users
    } catch (e: Exception) {
        null
    }
}

// groupリスト取得
fun getGroups(userId: Int): MutableList<Group>? {

    val groups: MutableList<Group> = mutableListOf()

    // userIdから所属グループを取得
    return try {
        transaction {
            (GroupMemberTable innerJoin GroupTable).slice(GroupTable.id, GroupTable.name).select {
                GroupMemberTable.user_id.eq(userId)
            }.forEach {
                groups += Group(it[GroupTable.id], it[GroupTable.name])
            }
        }
        groups
    } catch (e: Exception) {
        null
    }
}
