package cn.edu.sicnu.cardgame

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import cn.edu.sicnu.cardgame.view.PlayingCardView
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * UI 测试辅助：按"第几张牌 + 是否已翻开"定位牌桌上的某一张牌。
 */
fun withCardIndex(index: Int, faceUp: Boolean? = null): Matcher<View> =
    object : BoundedMatcher<View, PlayingCardView>(PlayingCardView::class.java) {

        override fun describeTo(description: Description) {
            description.appendText("card at index $index (faceUp=$faceUp)")
        }

        override fun matchesSafely(item: PlayingCardView): Boolean {
            if (item.cardIndex != index) {
                return false
            }
            return faceUp == null || item.isFaceUp == faceUp
        }
    }

/**
 * UI 测试辅助：断言 RecyclerView 适配器中的牌数。
 *
 * RecyclerView 只把屏幕内的格子实例化，用 hasChildCount 统计并不可靠，
 * 因此这里直接检查 Adapter 的 itemCount。
 */
fun withItemCount(count: Int): Matcher<View> =
    object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

        override fun describeTo(description: Description) {
            description.appendText("RecyclerView with $count items")
        }

        override fun matchesSafely(item: RecyclerView): Boolean =
            item.adapter?.itemCount == count
    }
