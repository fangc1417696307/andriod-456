package cn.edu.sicnu.cardgame.model

/**
 * 扑克牌实体类（Model 层）。
 *
 * 该类参照实验附件中的 Card.kt 实现，只补充了供 View 层读取花色、点数的只读属性，
 * 其余字段的封装方式、计分规则均与附件完全一致。
 *
 * 实验五在此基础上把匹配规则扩展为"三张牌"：一次提交两张已选中的牌，
 * 三张点数全同得 4 分，三张花色全同得 1 分，否则 0 分。
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

        /** 点数相同的得分 */
        const val RANK_MATCH_SCORE = 4

        /** 花色相同的得分 */
        const val SUIT_MATCH_SCORE = 1
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 计算本牌与 otherCards 的匹配得分。
     *
     * otherCards 为控制器传入的"其它已选中的牌"：
     *  - 传入 1 张：两张牌点数相同得 4 分，花色相同得 1 分，都不同得 0 分；
     *  - 传入 2 张：三张牌点数全同得 4 分，花色全同得 1 分，都不同得 0 分。
     *
     * 也就是说，三张牌必须"全同"才算匹配，只有其中两张相同是不能配对的。
     */
    fun match(otherCards: Array<Card>): Int {
        val group = otherCards.toList() + this
        if (group.size !in 2..3) {
            return 0
        }
        return when {
            group.all { it.rank == rank } -> RANK_MATCH_SCORE
            group.all { it.suit == suit } -> SUIT_MATCH_SCORE
            else -> 0
        }
    }
}
