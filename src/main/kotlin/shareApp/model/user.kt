package shareApp.model

import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

// userTable
object UserTable : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50).uniqueIndex()
    val password = varchar("password", 100)
}

data class User(
        var id: Int = 0,
        var name: String = "",
        var password: String = "",
        var add_task_flg: Boolean = false
)

data class ResponseUserData(
        var id: Int = 0,
        var name: String = "",
        var group: MutableList<Group>? = null,
        var add_task_flg: Boolean = false
)

data class ResponseUserDataWithToken(
        var id: Int = 0,
        var name: String = "",
        var group: MutableList<Group>? = null,
        var token: String? = null
)


// loginメソッド
fun login(u: User): ResponseUserDataWithToken {

    u.password = hashString("SHA-256", u.password)

    // userテーブルからselect
    transaction {
        UserTable.select {
            UserTable.name.eq(u.name) and UserTable.password.eq(u.password)
        }.forEach {
                    u.id = it[UserTable.id]
                    /* u.group_id = it[UserTable.group_id] */
                }
    }
    // name or passが違う時404
    if (u.id == 0) throw halt(404, "wrong name or pass")

    // token生成
    val token = createToken(u.id)
    // userが属しているグループ参照
    val groups = getGroups(findUserIdByToken(token))

    // res
    return ResponseUserDataWithToken(u.id, u.name, groups, token)
}

// createUserメソッド
fun addUser(u: User): ResponseUserData {

    // passのハッシュ化
    u.password = hashString("SHA-256", u.password)

    // insert
    transaction {
        try {
            u.id = UserTable.insert {
                it[name] = u.name
                it[password] = u.password
            } get UserTable.id
        } catch (e: Exception) {
            // nameの被りで400
            throw halt(400, "this name is already exist") //大概ユーザー名被り
        }
    }
    // res
    return ResponseUserData(u.id, u.name, null)
}

// getUserInfo
fun getUser(id: Int): ResponseUserData {
    var group: Group
    val group_id: MutableList<Group> = mutableListOf()
    lateinit var user: User

    // userIdからuserInfo取得
    transaction {
        UserTable.select {
            UserTable.id.eq(id)
        }.forEach {
            user = User(it[UserTable.id], it[UserTable.name]
                            , it[UserTable.password])
        }

        if (user == null) throw halt(404, "is not exist")
//      ここでエラー処理　書き直す TODO

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
    // res
    return ResponseUserData(user.id, user.name, group_id)
}

// userList取得
fun getUserList(group: Int): MutableList<GroupMember> {
    var user: GroupMember
    val users: MutableList<GroupMember> = mutableListOf() // groupMemberのリスト

    // groupIdからgroup参照してuserTableにjoin→groupMemberのname,id取得
    transaction {
        (GroupMemberTable innerJoin UserTable).slice(UserTable.id, UserTable.name).select {
            GroupMemberTable.group_id.eq(group)
        }.forEach {
            user = GroupMember(it[UserTable.id], it[UserTable.name])
            users += user
        }
    }
    return users
}

// groupリスト取得
fun getGroups(userId: Int): MutableList<Group> {

    val groups: MutableList<Group> = mutableListOf()

    // userIdから所属グループを取得
    transaction {
        (GroupMemberTable innerJoin GroupTable).slice(GroupTable.id, GroupTable.name).select {
            GroupMemberTable.user_id.eq(userId)
        }.forEach {
                    groups += Group(it[GroupTable.id], it[GroupTable.name])
                }
    }
    return groups
}
