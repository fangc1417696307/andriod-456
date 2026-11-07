package cn.edu.sicnu.cardgame.model

/**
 * 翻牌游戏的核心逻辑（Model 层）。
 *
 * 参照实验附件 CardMatchingGame.kt 实现：
 *  - 从牌堆中随机抽出 count 张牌作为牌桌；
 *  - 选牌扣 1 分，匹配成功按 match() 得分 * MATCH_BONUS 加分，
 *    匹配失败扣 MISMATCH_PENALTY 分；
 *  - 已匹配的牌不能再被选择。
 *
 * 为方便 View 层展示与单元测试，补充了 cardCount、matchedPairs 两个只读属性
 * 以及一个内部构造方法（用固定牌组构造，使单元测试结果可预期）。
 */
class CardMatchingGame(count: Int) {

    var score = 0
        private set

    private val cards: MutableList<Card>

    init {
        val deck = Deck()
        cards = mutableListOf()
        for (i in 1..count) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
    }

    /** 测试专用：用指定的牌组直接构造模型，便于编写结果确定的单元测试 */
    internal constructor(fixedCards: List<Card>) : this(fixedCards.size) {
        cards.clear()
        cards.addAll(fixedCards)
    }

    /** 牌桌上的牌数 */
    val cardCount: Int
        get() = cards.size

    /** 已经成功匹配的牌对数 */
    val matchedPairs: Int
        get() = cards.count { it.isMatched } / 2

    fun cardAtIndex(index: Int): Card {
        return cards.get(index)
    }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (!card.isMatched) {
            if (card.isChosen) {
                card.isChosen = false
            } else {
                for (otherCard in cards) {
                    if (otherCard.isChosen && !otherCard.isMatched) {
                        val matchScore = card.match(arrayOf(otherCard))
                        if (matchScore > 0) {
                            score += matchScore * MATCH_BONUS
                            otherCard.isMatched = true
                            card.isMatched = true
                        } else {
                            score -= MISMATCH_PENALTY
                            otherCard.isChosen = false
                        }
                        break
                    }
                }
                score -= COST_TO_CHOOSE
                card.isChosen = true
            }
        }
    }
}
