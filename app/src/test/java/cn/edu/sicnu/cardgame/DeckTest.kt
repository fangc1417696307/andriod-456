package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Model 层 Deck 的单元测试：验证一副牌 52 张、随机抽取不重复、抽空后返回 null。
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
}
