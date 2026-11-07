package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层 Deck 的单元测试：验证一副牌 52 张、随机抽取不重复、抽空后返回 null，
 * 以及实验五新增的 dealHand（保证每 3 张牌都能凑成一组）。
 */
class DeckTest {

    @Test
    fun newDeck_contains52Cards() {
        assertEquals(52, Deck().size)
    }

    @Test
    fun drawAllCards_returns52UniqueCards() {
        val deck = Deck()
        val drawn = mutableSetOf<String>()
        repeat(52) {
            val card = deck.drawRandomCard()
            assertNotNull(card)
            drawn.add(card.toString())
        }
        assertEquals(52, drawn.size)
        assertEquals(0, deck.size)
    }

    @Test
    fun drawFromEmptyDeck_returnsNull() {
        val deck = Deck()
        repeat(52) { deck.drawRandomCard() }
        assertNull(deck.drawRandomCard())
    }

    @Test
    fun dealHand24By3_returnsEightGroupsOfThree() {
        val deck = Deck()

        val hand = deck.dealHand(24, 3)

        assertEquals(24, hand.size)
        val groups = hand.groupBy { it.rankName }
        assertEquals(8, groups.size)
        assertTrue(groups.values.all { it.size == 3 })
        // 同一组的牌花色互不相同
        assertTrue(groups.values.all { group -> group.map { it.suitName }.distinct().size == 3 })
        // 发出的牌从牌堆中移除
        assertEquals(28, deck.size)
    }

    @Test
    fun dealHand12By3_returnsFourGroupsOfThree() {
        val hand = Deck().dealHand(12, 3)

        assertEquals(12, hand.size)
        assertEquals(4, hand.groupBy { it.rankName }.size)
    }
}
