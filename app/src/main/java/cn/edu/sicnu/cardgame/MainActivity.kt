package cn.edu.sicnu.cardgame

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.data.PlayerStore
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

/**
 * 主界面（1 个主 Activity）：显示当前玩家的基本信息，并提供进入另外两个界面的入口。
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
