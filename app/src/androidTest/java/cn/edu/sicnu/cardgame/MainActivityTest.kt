package cn.edu.sicnu.cardgame

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.cardgame.data.PlayerStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 主界面的 UI 测试（Espresso）：
 * 校验玩家信息的显示，以及两个二级界面的跳转。
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        PlayerStore.reset(context)
    }

    @Test
    fun showsCurrentPlayerInformation() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.tvName)).check(matches(withText(PlayerStore.name)))
        onView(withId(R.id.tvStudentId)).check(matches(withText(PlayerStore.studentId)))
        onView(withId(R.id.tvGender))
            .check(matches(withText(context.getString(R.string.gender_male))))
        onView(withId(R.id.tvPhone)).check(matches(withText(PlayerStore.phone)))
        onView(withId(R.id.tvEmail)).check(matches(withText(PlayerStore.email)))
        onView(withId(R.id.tvHighScore))
            .check(matches(withText(context.getString(R.string.score_format, 0))))
        onView(withId(R.id.btnEditInfo)).check(matches(isDisplayed()))
        onView(withId(R.id.btnPlayGame)).check(matches(isDisplayed()))
    }

    @Test
    fun clickEditInfo_opensInformationActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.btnEditInfo)).perform(scrollTo(), click())

        onView(withId(R.id.etName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }

    @Test
    fun clickPlayGame_opensCardGameActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.btnPlayGame)).perform(scrollTo(), click())

        onView(withId(R.id.cardGameView)).check(matches(isDisplayed()))
        onView(withId(R.id.tvScore))
            .check(matches(withText(context.getString(R.string.score_format, 0))))
    }
}
