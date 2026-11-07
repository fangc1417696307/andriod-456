package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层 Card 的单元测试：验证牌面字符串、花色颜色标记与匹配计分规则。
 */
class CardTest {

    @Test
    fun toString_returnsSuitAndRank() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals("♥A", card.toString())
    }

    @Test
    fun isRedSuit_heartsAndDiamonds_areRed() {
        assertTrue(Card("♥", "A").isRedSuit)
        assertTrue(Card("♦", "A").isRedSuit)
    }

    @Test
    fun isRedSuit_spadesAndClubs_areNotRed() {
        assertFalse(Card("♠", "A").isRedSuit)
        assertFalse(Card("♣", "A").isRedSuit)
    }

    @Test
    fun match_sameRank_returnsFour() {
        val card = Card("♥", "A")
        val otherCard = Card("♠", "A")
        assertEquals(4, card.match(arrayOf(otherCard)))
    }

    @Test
    fun match_onlySameSuit_returnsOne() {
        val card = Card("♥", "A")
        val otherCard = Card("♥", "5")
        assertEquals(1, card.match(arrayOf(otherCard)))
    }

    @Test
    fun match_differentRankAndSuit_returnsZero() {
        val card = Card("♥", "A")
        val otherCard = Card("♠", "5")
        assertEquals(0, card.match(arrayOf(otherCard)))
    }

    @Test
    fun match_moreThanOneCard_returnsZero() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♠", "A"), Card("♣", "A"))
        assertEquals(0, card.match(others))
    }

    @Test
    fun newCard_isNeitherChosenNorMatched() {
        val card = Card("♣", "K")
        assertFalse(card.isChosen)
        assertFalse(card.isMatched)
    }
}
