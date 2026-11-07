package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.controller.CardGameController
import cn.edu.sicnu.cardgame.model.Card
import cn.edu.sicnu.cardgame.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层 CardMatchingGame 的单元测试。
 *
 * 使用内部构造方法传入固定的牌组，使计分结果完全可预期。
 * 计分规则：选牌 -1，点数相同 +4*4，花色相同 +1*4，不匹配 -2。
 */
class CardMatchingGameTest {

    private fun gameOf(vararg cards: Card) = CardMatchingGame(cards.toList())

    @Test
    fun newGame_dealsRequestedNumberOfCardsAndStartsFromZero() {
        val game = CardMatchingGame(16)
        assertEquals(16, game.cardCount)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedPairs)
    }

    @Test
    fun chooseOneCard_costsOnePointAndTurnsFaceUp() {
        val game = gameOf(Card("♥", "A"), Card("♠", "K"))

        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun chooseSameCardTwice_turnsItBackAndKeepsScore() {
        val game = gameOf(Card("♥", "A"), Card("♠", "K"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun choosePairWithSameRank_scoresBonusAndMarksBothMatched() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        // -1（选第一张） + 4 * 4（点数相同） - 1（选第二张）= 14
        assertEquals(14, game.score)
        assertTrue(game.cardAtIndex(0).isMatched)
        assertTrue(game.cardAtIndex(1).isMatched)
        assertEquals(1, game.matchedPairs)
    }

    @Test
    fun choosePairWithSameSuitOnly_scoresSmallBonus() {
        val game = gameOf(Card("♥", "A"), Card("♥", "K"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        // -1 + 1 * 4 - 1 = 2
        assertEquals(2, game.score)
        assertEquals(1, game.matchedPairs)
    }

    @Test
    fun chooseMismatchedCards_appliesPenaltyAndTurnsPreviousCardBack() {
        val game = gameOf(Card("♥", "A"), Card("♠", "K"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        // -1 - 2（不匹配）- 1 = -4
        assertEquals(-4, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
        assertEquals(0, game.matchedPairs)
    }

    @Test
    fun matchedCard_cannotBeChosenAgain() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        val scoreAfterMatch = game.score

        game.chooseCardAtIndex(0)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun restartWithController_startsANewGameWithZeroScore() {
        val controller = CardGameController()
        controller.selectCard(0)
        assertEquals(-1, controller.model.score)
        val firstGame = controller.model

        controller.startNewGame()

        assertEquals(0, controller.model.score)
        assertEquals(16, controller.model.cardCount)
        assertNotSame(firstGame, controller.model)
    }
}
