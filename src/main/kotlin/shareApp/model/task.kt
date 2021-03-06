package shareApp.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.joda.time.DateTime
import java.util.*


// taskテーブル
object TaskTable : Table("tasks") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 50)
    val group_id = integer("group_id")
    val user_id = integer("user_id")
    val done = bool("done")
    val d_year = integer("dead_year").nullable()
    val d_month = integer("dead_month").nullable()
    val d_day = integer("dead_day").nullable()
    val limit_date = datetime("limit").nullable()
}

// 締め切り
data class DeadLine(
        var year: Int? = null,
        var month: Int? = null,
        var day: Int? = null
)

// task単体
data class Task(
        var id: Int? = 0,
        var title: String = "",
        var group_id: Int = 0,
        var user_id: Int = 0,
        var done: Boolean = false,
        var dead: DeadLine = DeadLine(),
        var limit_date: DateTime? = null
)

// taskリスト
data class TaskList(
        var main: MutableList<Task>? = null
)

// idからtask取得
fun getTask(id: Int): Task? {

    lateinit var task: Task
    return try {
        transaction {
            TaskTable.select {
                TaskTable.id.eq(id)
            }.orderBy(TaskTable.limit_date).forEach {
                task = Task(it[TaskTable.id], it[TaskTable.title]
                        , it[TaskTable.group_id], it[TaskTable.user_id], it[TaskTable.done],
                        DeadLine(it[TaskTable.d_year], it[TaskTable.d_month], it[TaskTable.d_day]), it[TaskTable.limit_date])
            }
        }
        task
    } catch (e: Exception) {
        null
    }
}

// groupのtaskList取得
fun getTaskListByGroupId(group_id: Int): TaskList? {

    lateinit var task: Task
    val list: MutableList<Task> = mutableListOf()
    var tasks = TaskList()
//    lateinit var main: Tasks

    // groupIdが一致するタスクを取得→リストに格納
    return try {
        transaction {
            TaskTable.select {
                TaskTable.group_id.eq(group_id)
            }.orderBy(TaskTable.limit_date).forEach {
                task = Task(it[TaskTable.id], it[TaskTable.title],
                        it[TaskTable.group_id], it[TaskTable.user_id], it[TaskTable.done],
                        DeadLine(it[TaskTable.d_year], it[TaskTable.d_month], it[TaskTable.d_day]), it[TaskTable.limit_date])
                list += task
                tasks = TaskList(list)
            }
        }
        tasks
    } catch (e: Exception) {
        null
    }
}

// userIdからtaskList取得
fun getTaskListByUserId(user_id: Int): TaskList? {

    lateinit var task: Task
    val list: MutableList<Task> = mutableListOf()
    var tasks = TaskList()

    return try {
        transaction {
            TaskTable.select {
                TaskTable.user_id.eq(user_id)
            }.orderBy(TaskTable.limit_date).forEach {
                task = Task(it[TaskTable.id], it[TaskTable.title],
                        it[TaskTable.group_id], it[TaskTable.user_id], it[TaskTable.done],
                        DeadLine(it[TaskTable.d_year], it[TaskTable.d_month], it[TaskTable.d_day]), it[TaskTable.limit_date])
                list += task
                tasks = TaskList(list)
            }
        }
        tasks
    } catch (e: Exception) {
        null
    }
}

// 全タスク取得（このメソッドいる？）
fun getAllTasks(): TaskList? {

    lateinit var task: Task
    val list: MutableList<Task> = mutableListOf()
    var tasks = TaskList()

    return try {
        transaction {
            TaskTable.selectAll()
                    .orderBy(TaskTable.limit_date).forEach {
                        task = Task(it[TaskTable.id], it[TaskTable.title],
                                it[TaskTable.group_id], it[TaskTable.user_id], it[TaskTable.done],
                                DeadLine(it[TaskTable.d_year], it[TaskTable.d_month], it[TaskTable.d_day]), it[TaskTable.limit_date])
                        list += task
                        tasks = TaskList(list)
                    }

        }
        tasks
    } catch (e: Exception) {
        null
    }
}

// task追加
fun addTask(task: Task, userId: Int): Task? {

    val cal = Calendar.getInstance()

    // datetimeのフォーマット
    cal.set(Calendar.MONTH, task.dead.month!! - 1)
    cal.set(Calendar.YEAR, task.dead.year!!)
    cal.set(Calendar.DAY_OF_MONTH, task.dead.day!!)
    task.limit_date = DateTime(cal)
    return try {
        transaction {
            task.id = TaskTable.insert {
                it[title] = task.title
                it[group_id] = task.group_id
                it[user_id] = userId
                it[done] = task.done
                it[d_year] = task.dead.year
                it[d_month] = task.dead.month
                it[d_day] = task.dead.day
                it[limit_date] = DateTime(cal)
            } get TaskTable.id
        }
        task
    } catch (e: Exception) {
        return null
    }

}

//task削除
fun deleteTask(userId: Int, taskId: Int): Boolean {

    val task = getTask(taskId)

    task?.let {
        if (userId != task.user_id) {
            return false
        } } ?: return false


    return try {
        transaction {
            TaskTable.deleteWhere {
                TaskTable.id.eq(task.id!!)
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}
