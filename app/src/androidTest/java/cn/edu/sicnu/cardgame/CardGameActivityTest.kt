package cn.edu.sicnu.cardgame

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.cardgame.data.PlayerStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 翻牌游戏界面的 UI 测试（Espresso）：
 * 校验牌的渲染数量、翻牌扣分、重复点击翻回、重新开始与退出。
 */
@RunWith(AndroidJUnit4::class)
class CardGameActivityTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun scoreText(score: Int): String = context.getString(R.string.score_format, score)

    @Before
    fun setUp() {
        PlayerStore.reset(context)
    }

    @Test
    fun gameScreen_showsSixteenFaceDownCardsAndZeroScore() {
        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withId(R.id.cardGameView)).check(matches(hasChildCount(16)))
        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(0))))
        onView(withCardIndex(0, faceUp = false)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCard_turnsItFaceUpAndCostsOnePoint() {
        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withCardIndex(0)).perform(click())

        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(-1))))
        onView(withCardIndex(0, faceUp = true)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSameCardTwice_turnsItBackWithoutChangingScore() {
        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withCardIndex(3)).perform(click())
        onView(withCardIndex(3)).perform(click())

        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(-1))))
        onView(withCardIndex(3, faceUp = false)).check(matches(isDisplayed()))
    }

    @Test
    fun clickRestart_resetsScoreAndHidesAllCards() {
        ActivityScenario.launch(CardGameActivity::class.java)
        onView(withCardIndex(0)).perform(click())
        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(-1))))

        onView(withId(R.id.btnRestart)).perform(click())

        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(0))))
        onView(withCardIndex(0, faceUp = false)).check(matches(isDisplayed()))
    }

    @Test
    fun clickExit_returnsToMainScreen() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnPlayGame)).perform(scrollTo(), click())
        onView(withId(R.id.cardGameView)).check(matches(isDisplayed()))

        onView(withId(R.id.btnExit)).perform(click())

        // 退出后回到主界面
        onView(withId(R.id.tvName)).check(matches(isDisplayed()))
    }
}
