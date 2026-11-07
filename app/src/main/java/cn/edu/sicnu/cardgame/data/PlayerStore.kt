package cn.edu.sicnu.cardgame.data

import android.content.Context
import androidx.annotation.DrawableRes
import cn.edu.sicnu.cardgame.R

/**
 * 当前玩家信息的仓库（单例，内存存储）。
 *
 * 实验要求"修改后的信息无需保存到文件中"，因此这里把数据放在进程内的单例对象里，
 * 主界面、信息修改界面、翻牌游戏界面三个 Activity 共享同一份数据。
 */
object PlayerStore {

    enum class Gender { MALE, FEMALE }

    var name: String = ""
    var studentId: String = ""
    var gender: Gender = Gender.MALE
    var phone: String = ""
    var email: String = ""

    /** 爱好使用有序集合保存，保证显示顺序稳定 */
    var hobbies: Set<String> = linkedSetOf()

    @DrawableRes
    var avatarRes: Int = R.drawable.avatar_blue

    var highScore: Int = 0

    private var initialised = false

    /** 用资源文件中的默认（示例）数据初始化，默认值同样来自 strings.xml */
    fun init(context: Context) {
        if (initialised) {
            return
        }
        val resources = context.applicationContext.resources
        name = resources.getString(R.string.default_name)
        studentId = resources.getString(R.string.default_student_id)
        phone = resources.getString(R.string.default_phone)
        email = resources.getString(R.string.default_email)
        hobbies = linkedSetOf(
            resources.getString(R.string.hobby_travel),
            resources.getString(R.string.hobby_reading)
        )
        initialised = true
    }

    /** 还原为默认数据，供单元测试/UI 测试使用 */
    fun reset(context: Context) {
        initialised = false
        gender = Gender.MALE
        avatarRes = R.drawable.avatar_blue
        highScore = 0
        init(context)
    }
}
