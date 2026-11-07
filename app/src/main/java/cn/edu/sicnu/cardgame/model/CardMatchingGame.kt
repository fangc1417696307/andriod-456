package cn.edu.sicnu.cardgame.model

/**
 * 翻牌游戏的核心逻辑（Model 层）。
 *
 * 参照实验附件 CardMatchingGame.kt 实现，并按实验五的要求改为"三张牌"配对：
 *  - 从牌堆中发出一桌保证可以配对的牌；
 *  - 每选中一张牌扣 1 分；当已选中的牌凑够 [matchCount] 张时交给 [Card.match] 判定，
 *    匹配成功按 match() 得分 * MATCH_BONUS 加分并把这组牌锁定，
 *    匹配失败扣 MISMATCH_PENALTY 分并把之前选中的牌翻回去；
 *  - 已匹配的牌不能再被选择；
 *  - [reset] 可以重新发牌并把分数清零。
 *
 * 为方便 View 层展示与单元测试，补充了 cardCount、groupCount、matchedGroups、cards
 * 等只读属性，以及一个内部构造方法（用固定牌组构造，使单元测试结果可预期）。
 */
class CardMatchingGame(count: Int, val matchCount: Int = DEFAULT_MATCH_COUNT) {

    var score = 0
        private set

    /** 发牌总数，reset() 时按同样的数量重新发牌 */
    private val tableSize = count

    private val table = mutableListOf<Card>()

    init {
        require(count > 0 && count % matchCount == 0) {
            "牌数必须是 $matchCount 的正整数倍"
        }
        dealCards()
    }

    /** 测试专用：用指定的牌组直接构造模型，便于编写结果确定的单元测试 */
    internal constructor(fixedCards: List<Card>, matchCount: Int = DEFAULT_MATCH_COUNT) :
        this(fixedCards.size, matchCount) {
        table.clear()
        table.addAll(fixedCards)
    }

    /** 牌桌上的牌数 */
    val cardCount: Int
        get() = table.size

    /** 牌桌上共分几组（三张牌玩法下 24 张牌是 8 组） */
    val groupCount: Int
        get() = if (matchCount == 0) 0 else table.size / matchCount

    /** 已经成功配对的组数 */
    val matchedGroups: Int
        get() = table.count { it.isMatched } / matchCount

    /** 牌桌上的牌，供 RecyclerView 适配器读取 */
    val cards: List<Card>
        get() = table

    fun cardAtIndex(index: Int): Card {
        return table.get(index)
    }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /** 重新开始一局：重新发牌并把分数清零（实验五要求新增的方法） */
    fun reset() {
        score = 0
        dealCards()
    }

    /**
     * 选中第 index 张牌。
     *
     * 先收集"已经选中但还没配对"的牌，凑够 matchCount - 1 张时与当前牌一起判定：
     * 匹配则整组锁定并加分，不匹配则扣分并把这组已选的牌翻回去。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) {
            return
        }
        // 再次点击已翻开的牌，只是把它翻回去
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val otherChosenCards = table.filter { it.isChosen && !it.isMatched }
        if (otherChosenCards.size == matchCount - 1) {
            val matchScore = card.match(otherChosenCards.toTypedArray())
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                otherChosenCards.forEach { it.isMatched = true }
                card.isMatched = true
            } else {
                score -= MISMATCH_PENALTY
                otherChosenCards.forEach { it.isChosen = false }
            }
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true
    }

    /** 重新发一桌牌，保证每 [matchCount] 张牌都能凑成一组 */
    private fun dealCards() {
        table.clear()
        table.addAll(Deck().dealHand(tableSize, matchCount))
    }

    companion object {
        /** 三张牌玩法：每凑够 3 张才判定一次 */
        const val DEFAULT_MATCH_COUNT = 3
    }
}
