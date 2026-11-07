package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层 Card 的单元测试：验证牌面字符串、花色颜色标记与匹配计分规则。
 *
 * 实验五的规则：三张牌点数全同得 4 分，花色全同得 1 分，否则 0 分。
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
    fun match_threeCardsWithSameRank_returnsFour() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♠", "A"), Card("♣", "A"))

        assertEquals(Card.RANK_MATCH_SCORE, card.match(others))
    }

    @Test
    fun match_threeCardsWithSameSuit_returnsOne() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♥", "5"), Card("♥", "K"))

        assertEquals(Card.SUIT_MATCH_SCORE, card.match(others))
    }

    @Test
    fun match_threeCardsWithOnlyTwoSameRank_returnsZero() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♠", "A"), Card("♣", "7"))

        assertEquals(0, card.match(others))
    }

    @Test
    fun match_threeCardsWithOnlyTwoSameSuit_returnsZero() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♥", "7"), Card("♠", "7"))

        assertEquals(0, card.match(others))
    }

    @Test
    fun match_twoCardsWithSameRank_returnsFour() {
        val card = Card("♥", "A")

        assertEquals(Card.RANK_MATCH_SCORE, card.match(arrayOf(Card("♠", "A"))))
    }

    @Test
    fun match_twoCardsWithSameSuit_returnsOne() {
        val card = Card("♥", "A")

        assertEquals(Card.SUIT_MATCH_SCORE, card.match(arrayOf(Card("♥", "5"))))
    }

    @Test
    fun match_moreThanThreeCards_returnsZero() {
        val card = Card("♥", "A")
        val others = arrayOf(Card("♠", "A"), Card("♣", "A"), Card("♦", "A"))

        assertEquals(0, card.match(others))
    }

    @Test
    fun newCard_isNeitherChosenNorMatched() {
        val card = Card("♣", "K")
        assertFalse(card.isChosen)
        assertFalse(card.isMatched)
    }
}
