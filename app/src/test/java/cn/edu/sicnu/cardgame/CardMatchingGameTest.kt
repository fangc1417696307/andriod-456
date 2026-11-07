package cn.edu.sicnu.cardgame

import cn.edu.sicnu.cardgame.controller.CardGameController
import cn.edu.sicnu.cardgame.model.Card
import cn.edu.sicnu.cardgame.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层 CardMatchingGame 的单元测试。
 *
 * 使用内部构造方法传入固定的牌组，使计分结果完全可预期。
 * 三张牌玩法计分规则：选牌 -1，三张点数全同 +4*4，三张花色全同 +1*4，不匹配 -2。
 */
class CardMatchingGameTest {

    private fun gameOf(vararg cards: Card) = CardMatchingGame(cards.toList())

    @Test
    fun newGame_dealsRequestedNumberOfCardsAndStartsFromZero() {
        val game = CardMatchingGame(24)

        assertEquals(24, game.cardCount)
        assertEquals(8, game.groupCount)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedGroups)
    }

    @Test
    fun newGame_dealsCardsThatCanAlwaysBeGroupedByThree() {
        val game = CardMatchingGame(24)

        val countByRank = game.cards.groupingBy { it.rankName }.eachCount()
        assertEquals(8, countByRank.size)
        assertTrue(countByRank.values.all { it == CardMatchingGame.DEFAULT_MATCH_COUNT })
    }

    @Test
    fun chooseOneCard_costsOnePointAndTurnsFaceUp() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"), Card("♣", "A"))

        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun chooseSameCardTwice_turnsItBackAndKeepsScore() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"), Card("♣", "A"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun chooseThreeCardsWithSameRank_scoresBonusAndMarksAllMatched() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"), Card("♣", "A"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        // -1（第一张） -1（第二张） + 4 * 4（三张点数相同） -1（第三张）= 13
        assertEquals(13, game.score)
        assertTrue(game.cards.all { it.isMatched })
        assertEquals(1, game.matchedGroups)
    }

    @Test
    fun chooseThreeCardsWithSameSuit_scoresSmallBonus() {
        val game = gameOf(Card("♥", "A"), Card("♥", "5"), Card("♥", "K"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        // -1 -1 + 1 * 4 -1 = 1
        assertEquals(1, game.score)
        assertEquals(1, game.matchedGroups)
    }

    @Test
    fun chooseThreeCardsWithOnlyTwoSameRank_appliesPenaltyAndTurnsOthersBack() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"), Card("♣", "7"))

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        // -1 -1 - 2（不匹配） -1 = -5
        assertEquals(-5, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(1).isChosen)
        assertTrue(game.cardAtIndex(2).isChosen)
        assertEquals(0, game.matchedGroups)
    }

    @Test
    fun matchedCards_cannotBeChosenAgain() {
        val game = gameOf(Card("♥", "A"), Card("♠", "A"), Card("♣", "A"))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        val scoreAfterMatch = game.score

        game.chooseCardAtIndex(0)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun reset_clearsScoreAndDealsFaceDownCardsAgain() {
        val game = CardMatchingGame(24)
        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)

        game.reset()

        assertEquals(0, game.score)
        assertEquals(24, game.cardCount)
        assertEquals(0, game.matchedGroups)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
    }

    @Test
    fun restartWithController_resetsTheSameGameModel() {
        val controller = CardGameController()
        controller.selectCard(0)
        assertEquals(-1, controller.model.score)
        val game = controller.model

        controller.startNewGame()

        assertSame(game, controller.model)
        assertEquals(0, controller.model.score)
        assertEquals(24, controller.model.cardCount)
    }
}
