package cn.edu.sicnu.cardgame

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.cardgame.data.PlayerStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 信息修改界面的 UI 测试（Espresso）：
 * 校验"保存"后主界面同步刷新、"取消"不保存、输入校验以及头像更换。
 */
@RunWith(AndroidJUnit4::class)
class PersonInformationActivityTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        PlayerStore.reset(context)
    }

    @Test
    fun saveNewName_mainScreenShowsUpdatedName() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnEditInfo)).perform(scrollTo(), click())

        onView(withId(R.id.etName)).perform(replaceText("李雷"))
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withId(R.id.tvName)).check(matches(withText("李雷")))
        assertEquals("李雷", PlayerStore.name)
    }

    @Test
    fun saveEmptyName_showsErrorAndKeepsOriginalName() {
        ActivityScenario.launch(PersonInformationActivity::class.java)

        onView(withId(R.id.etName)).perform(replaceText(""))
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withText(R.string.error_name_empty)).check(matches(isDisplayed()))
        assertEquals(context.getString(R.string.default_name), PlayerStore.name)
    }

    @Test
    fun invalidPhone_showsError() {
        ActivityScenario.launch(PersonInformationActivity::class.java)

        onView(withId(R.id.etPhone)).perform(replaceText("12345"))
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withText(R.string.error_phone_invalid)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCancel_doesNotSaveChanges() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnEditInfo)).perform(scrollTo(), click())

        onView(withId(R.id.etName)).perform(replaceText("韩梅梅"))
        onView(withId(R.id.btnCancel)).perform(scrollTo(), click())

        onView(withId(R.id.tvName))
            .check(matches(withText(context.getString(R.string.default_name))))
    }

    @Test
    fun saveGenderAndHobbies_updatesMainScreen() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnEditInfo)).perform(scrollTo(), click())

        onView(withId(R.id.rbFemale)).perform(scrollTo(), click())
        onView(withId(R.id.cbHobbyGame)).perform(scrollTo(), click())
        onView(withId(R.id.cbHobbyShopping)).perform(scrollTo(), click())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        onView(withId(R.id.tvGender))
            .check(matches(withText(context.getString(R.string.gender_female))))
        val separator = context.getString(R.string.hobby_separator)
        val expectedHobbies = listOf(
            R.string.hobby_travel,
            R.string.hobby_reading,
            R.string.hobby_game,
            R.string.hobby_shopping
        ).joinToString(separator) { context.getString(it) }
        onView(withId(R.id.tvHobby)).check(matches(withText(expectedHobbies)))
    }

    @Test
    fun changeAvatarAndSave_updatesAvatarResource() {
        ActivityScenario.launch(PersonInformationActivity::class.java)

        onView(withId(R.id.btnChangeAvatar)).perform(click())
        onView(withId(R.id.avatarOptionTeal)).perform(click())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())

        assertEquals(R.drawable.avatar_teal, PlayerStore.avatarRes)
    }
}
