package cn.edu.sicnu.cardgame.model

import java.util.Collections
import java.util.Random

/**
 * 一副 52 张的扑克牌（Model 层），与实验附件中的 Deck.kt 实现一致。
 *
 * 实验五的三张牌玩法需要"一定能配对"的牌桌：如果仍然从 52 张牌里随便抽 24 张，
 * 很难保证桌面上正好凑出 8 组点数相同的三张牌，游戏很可能无解。
 * 因此新增 [dealHand]，按"点数分组、每组随机取若干张不同花色"的方式发牌。
 */
class Deck() {

    private val cards = mutableListOf<Card>()
    private val r = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                val card = Card(suit = suit, rank = rank)
                cards.add(card)
            }
        }
    }

    /** 剩余的牌数 */
    val size: Int
        get() = cards.size

    /** 随机抽一张牌，牌堆为空时返回 null */
    fun drawRandomCard(): Card? {
        var randomCard: Card? = null
        if (cards.size > 0) {
            randomCard = cards.removeAt(r.nextInt(cards.size))
        }
        return randomCard
    }

    /**
     * 发一手保证可以配对的牌：把牌按点数分成 13 组，随机挑出若干组，
     * 每组再从四种花色里随机取 [groupSize] 张，因此每 [groupSize] 张牌都能凑成一组。
     *
     * 例：dealHand(24, 3) 得到 8 组、每组 3 张点数相同的牌。
     *
     * @param cardCount 要发的总牌数，必须是 groupSize 的整数倍
     * @param groupSize 每组需要几张牌（三张牌玩法为 3）
     * @return 已经打乱顺序的牌列表，抽走的牌会从牌堆中移除
     */
    fun dealHand(cardCount: Int, groupSize: Int): List<Card> {
        require(cardCount > 0 && groupSize > 0 && cardCount % groupSize == 0) {
            "cardCount 必须是 groupSize 的正整数倍"
        }

        val cardsByRank = cards.groupBy { it.rankName }
        val ranks = cardsByRank.keys.toMutableList()
        Collections.shuffle(ranks, r)

        val hand = mutableListOf<Card>()
        for (rank in ranks) {
            if (hand.size >= cardCount) {
                break
            }
            val group = cardsByRank.getValue(rank).toMutableList()
            Collections.shuffle(group, r)
            hand.addAll(group.take(groupSize))
        }

        require(hand.size >= cardCount) { "牌堆剩余牌数不足以发出 $cardCount 张牌" }
        val result = hand.subList(0, cardCount).toMutableList()

        cards.removeAll(result)
        // 打乱顺序，避免同一组的牌相邻出现
        Collections.shuffle(result, r)
        return result
    }
}
