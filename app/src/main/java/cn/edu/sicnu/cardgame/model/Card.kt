package cn.edu.sicnu.cardgame.model

/**
 * 扑克牌实体类（Model 层）。
 *
 * 该类参照实验附件中的 Card.kt 实现，只补充了供 View 层读取花色、点数的只读属性，
 * 其余字段的封装方式、计分规则均与附件完全一致。
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {

    /** 花色，例如 "♥" */
    val suitName: String
        get() = suit

    /** 点数，例如 "A"、"10" */
    val rankName: String
        get() = rank

    /** 是否为红色花色（红桃、方块），View 层据此决定文字颜色 */
    val isRedSuit: Boolean
        get() = suit == validSuits[0] || suit == validSuits[1]

    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 计算本牌与 otherCards 的匹配得分：
     * 点数相同得 4 分，花色相同得 1 分，否则 0 分。
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        if (otherCards.size == 1) {
            val otherCard = otherCards[0]
            if (otherCard.rank == rank) {
                score = 4
            } else if (otherCard.suit == suit) {
                score = 1
            }
        }
        return score
    }
}
