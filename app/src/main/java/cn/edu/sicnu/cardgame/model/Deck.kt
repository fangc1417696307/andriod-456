package cn.edu.sicnu.cardgame.model

import java.util.Random

/**
 * 一副 52 张的扑克牌（Model 层），与实验附件中的 Deck.kt 实现一致。
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
}
