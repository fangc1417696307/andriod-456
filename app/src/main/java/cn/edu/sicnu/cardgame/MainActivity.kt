package cn.edu.sicnu.cardgame

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.data.PlayerStore
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView

/**
 * 主界面（1 个主 Activity）：显示当前玩家的基本信息，并提供进入另外两个界面的入口。
 *
 * 实验五新增菜单：ActionBar 右侧的"关于"菜单项，点击后弹出关于对话框。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btnEditInfo).setOnClickListener {
            startActivity(Intent(this, PersonInformationActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnPlayGame).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    /** 从其它界面返回时重新读取玩家信息，保证修改后的数据立即生效 */
    override fun onResume() {
        super.onResume()
        showPlayerInfo()
    }

    /** 加载菜单资源（res/menu/menu_main.xml） */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** 点击"关于"菜单项时弹出对话框 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 关于对话框：内容来自 dialog_about.xml，标题、版本、作者等文字全部取自字符串资源，
     * 作者与学号直接读取当前玩家信息，改完个人信息后对话框也会跟着变。
     */
    private fun showAboutDialog() {
        val content = layoutInflater.inflate(R.layout.dialog_about, null)
        content.findViewById<TextView>(R.id.tvAboutVersion).text =
            getString(R.string.about_version, getString(R.string.app_version))
        content.findViewById<TextView>(R.id.tvAboutAuthor).text =
            getString(R.string.about_author, PlayerStore.name)
        content.findViewById<TextView>(R.id.tvAboutStudentId).text =
            getString(R.string.about_student_id, PlayerStore.studentId)

        MaterialAlertDialogBuilder(this)
            .setView(content)
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    private fun showPlayerInfo() {
        findViewById<ShapeableImageView>(R.id.ivAvatar).setImageResource(PlayerStore.avatarRes)
        findViewById<TextView>(R.id.tvName).text = PlayerStore.name
        findViewById<TextView>(R.id.tvStudentId).text = PlayerStore.studentId
        findViewById<TextView>(R.id.tvGender).text = getString(
            if (PlayerStore.gender == PlayerStore.Gender.MALE) {
                R.string.gender_male
            } else {
                R.string.gender_female
            }
        )
        findViewById<TextView>(R.id.tvPhone).text = PlayerStore.phone
        findViewById<TextView>(R.id.tvEmail).text = PlayerStore.email
        findViewById<TextView>(R.id.tvHobby).text = hobbiesText()
        findViewById<TextView>(R.id.tvHighScore).text =
            getString(R.string.score_format, PlayerStore.highScore)
    }

    private fun hobbiesText(): String {
        if (PlayerStore.hobbies.isEmpty()) {
            return getString(R.string.empty_value)
        }
        return PlayerStore.hobbies.joinToString(getString(R.string.hobby_separator))
    }
}
