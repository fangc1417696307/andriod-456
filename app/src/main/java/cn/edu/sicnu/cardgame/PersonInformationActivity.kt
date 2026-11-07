package cn.edu.sicnu.cardgame

import android.os.Bundle
import android.util.Patterns
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.data.PlayerStore
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * 信息修改界面（二级 Activity）：修改玩家姓名、学号、电话、邮件、性别、爱好与头像。
 *
 * 修改结果保存在内存中的 [PlayerStore]（按要求无需写入文件），
 * 只有点击"保存"才写回数据，点击"取消"直接结束界面、不做任何修改。
 */
class PersonInformationActivity : AppCompatActivity() {

    private lateinit var avatarView: ShapeableImageView
    private lateinit var tilName: TextInputLayout
    private lateinit var tilStudentId: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etStudentId: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton

    /** 爱好复选框与对应字符串资源的映射 */
    private lateinit var hobbyCheckBoxes: List<Pair<CheckBox, Int>>

    /** 界面上暂存的头像，点击"保存"后才写回 PlayerStore */
    private var pendingAvatarRes: Int = PlayerStore.avatarRes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_information)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
        fillForm()

        findViewById<ImageView>(R.id.btnChangeAvatar).setOnClickListener { showAvatarPicker() }
        avatarView.setOnClickListener { showAvatarPicker() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { savePlayerInfo() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun bindViews() {
        avatarView = findViewById(R.id.ivAvatar)
        tilName = findViewById(R.id.tilName)
        tilStudentId = findViewById(R.id.tilStudentId)
        tilPhone = findViewById(R.id.tilPhone)
        tilEmail = findViewById(R.id.tilEmail)
        etName = findViewById(R.id.etName)
        etStudentId = findViewById(R.id.etStudentId)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)

        hobbyCheckBoxes = listOf(
            findViewById<CheckBox>(R.id.cbHobbyTravel) to R.string.hobby_travel,
            findViewById<CheckBox>(R.id.cbHobbyReading) to R.string.hobby_reading,
            findViewById<CheckBox>(R.id.cbHobbyGame) to R.string.hobby_game,
            findViewById<CheckBox>(R.id.cbHobbyShopping) to R.string.hobby_shopping
        )
    }

    /** 把 PlayerStore 中的当前信息填充到界面控件上 */
    private fun fillForm() {
        avatarView.setImageResource(pendingAvatarRes)
        etName.setText(PlayerStore.name)
        etStudentId.setText(PlayerStore.studentId)
        etPhone.setText(PlayerStore.phone)
        etEmail.setText(PlayerStore.email)

        if (PlayerStore.gender == PlayerStore.Gender.FEMALE) {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }

        for ((checkBox, stringRes) in hobbyCheckBoxes) {
            checkBox.isChecked = PlayerStore.hobbies.contains(getString(stringRes))
        }
    }

    /** 校验输入并保存到 PlayerStore */
    private fun savePlayerInfo() {
        val name = etName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            tilName.error = getString(R.string.error_name_empty)
            return
        }
        tilName.error = null

        val studentId = etStudentId.text?.toString()?.trim().orEmpty()
        if (studentId.isEmpty()) {
            tilStudentId.error = getString(R.string.error_student_id_empty)
            return
        }
        tilStudentId.error = null

        val phone = etPhone.text?.toString()?.trim().orEmpty()
        if (!PHONE_PATTERN.matches(phone)) {
            tilPhone.error = getString(R.string.error_phone_invalid)
            return
        }
        tilPhone.error = null

        val email = etEmail.text?.toString()?.trim().orEmpty()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_email_invalid)
            return
        }
        tilEmail.error = null

        PlayerStore.name = name
        PlayerStore.studentId = studentId
        PlayerStore.phone = phone
        PlayerStore.email = email
        PlayerStore.gender = if (rbFemale.isChecked) {
            PlayerStore.Gender.FEMALE
        } else {
            PlayerStore.Gender.MALE
        }
        PlayerStore.hobbies = hobbyCheckBoxes
            .filter { (checkBox, _) -> checkBox.isChecked }
            .map { (_, stringRes) -> getString(stringRes) }
            .toCollection(linkedSetOf())
        PlayerStore.avatarRes = pendingAvatarRes

        Toast.makeText(this, R.string.msg_info_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    /** 弹出头像选择对话框 */
    private fun showAvatarPicker() {
        val content = layoutInflater.inflate(R.layout.dialog_avatar, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.avatar_option_title)
            .setView(content)
            .setNegativeButton(R.string.action_cancel, null)
            .create()

        content.findViewById<ImageView>(R.id.avatarOptionBlue).setOnClickListener {
            selectAvatar(R.drawable.avatar_blue)
            dialog.dismiss()
        }
        content.findViewById<ImageView>(R.id.avatarOptionTeal).setOnClickListener {
            selectAvatar(R.drawable.avatar_teal)
            dialog.dismiss()
        }
        content.findViewById<ImageView>(R.id.avatarOptionOrange).setOnClickListener {
            selectAvatar(R.drawable.avatar_orange)
            dialog.dismiss()
        }
        content.findViewById<ImageView>(R.id.avatarOptionPurple).setOnClickListener {
            selectAvatar(R.drawable.avatar_purple)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun selectAvatar(drawableRes: Int) {
        pendingAvatarRes = drawableRes
        avatarView.setImageResource(drawableRes)
    }

    private companion object {
        /** 中国大陆手机号：1 开头，第二位 3-9，共 11 位 */
        val PHONE_PATTERN = Regex("^1[3-9]\\d{9}$")
    }
}
