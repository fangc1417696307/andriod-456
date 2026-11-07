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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 翻牌游戏界面的 UI 测试（Espresso）：
 * 校验 RecyclerView 的牌数、翻牌扣分、重复点击翻回、三张牌配对、重新开始与退出。
 */
@RunWith(AndroidJUnit4::class)
class CardGameActivityTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun scoreText(score: Int): String = context.getString(R.string.score_format, score)

    private fun groupsText(matched: Int, total: Int): String =
        context.getString(R.string.groups_format, matched, total)

    @Before
    fun setUp() {
        PlayerStore.reset(context)
    }

    @Test
    fun gameScreen_showsTwentyFourFaceDownCardsAndZeroScore() {
        ActivityScenario.launch(CardGameActivity::class.java)

        onView(withId(R.id.rvCards)).check(matches(withItemCount(24)))
        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(0))))
        onView(withId(R.id.tvGroups)).check(matches(withText(groupsText(0, 8))))
        onView(withCardIndex(0, faceUp = false)).check(matches(isDisplayed()))
        onView(withCardIndex(23, faceUp = false)).check(matches(isDisplayed()))
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
    fun clickThreeCardsOfSameRank_marksThemMatchedAndScoresBonus() {
        val scenario = ActivityScenario.launch(CardGameActivity::class.java)
        val triple = matchingTripleIndices(scenario)

        triple.forEach { onView(withCardIndex(it)).perform(click()) }

        // -1 -1 + 4 * 4 -1 = 13
        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(13))))
        onView(withId(R.id.tvGroups)).check(matches(withText(groupsText(1, 8))))
        triple.forEach { onView(withCardIndex(it)).check(matches(isDisplayed())) }
    }

    @Test
    fun clickRestart_resetsScoreAndHidesAllCards() {
        ActivityScenario.launch(CardGameActivity::class.java)
        onView(withCardIndex(0)).perform(click())
        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(-1))))

        onView(withId(R.id.btnRestart)).perform(click())

        onView(withId(R.id.tvScore)).check(matches(withText(scoreText(0))))
        onView(withId(R.id.tvGroups)).check(matches(withText(groupsText(0, 8))))
        onView(withCardIndex(0, faceUp = false)).check(matches(isDisplayed()))
    }

    @Test
    fun clickExit_returnsToMainScreen() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnPlayGame)).perform(scrollTo(), click())
        onView(withId(R.id.rvCards)).check(matches(isDisplayed()))

        onView(withId(R.id.btnExit)).perform(click())

        // 退出后回到主界面
        onView(withId(R.id.tvName)).check(matches(isDisplayed()))
    }

    /** 从模型里找出第一组点数相同的三张牌的下标，供"三张牌配对"用例点击 */
    private fun matchingTripleIndices(scenario: ActivityScenario<CardGameActivity>): List<Int> {
        var indices: List<Int> = emptyList()
        scenario.onActivity { activity ->
            indices = activity.controller.model.cards
                .withIndex()
                .groupBy { it.value.rankName }
                .values
                .first { it.size == 3 }
                .map { it.index }
        }
        assertEquals(3, indices.size)
        return indices
    }
}
