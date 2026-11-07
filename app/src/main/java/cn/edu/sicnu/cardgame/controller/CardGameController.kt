package cn.edu.sicnu.cardgame.controller

import cn.edu.sicnu.cardgame.model.CardMatchingGame

/**
 * 翻牌游戏控制器（Controller 层）。
 *
 * 职责：
 *  1. 持有模型 [CardMatchingGame]，是模型唯一的修改入口；
 *  2. 接收 View 层的交互（点牌、重新开始）；
 *  3. 处理完模型后通过 [Listener] 通知界面刷新，实现"模型与视图同步"。
 *
 * 控制器不直接引用任何 Activity / View / RecyclerView，便于单元测试。
 */
class CardGameController(
    private val cardCount: Int = DEFAULT_CARD_COUNT,
    private val matchCount: Int = DEFAULT_MATCH_COUNT
) {

    var model: CardMatchingGame = CardMatchingGame(cardCount, matchCount)
        private set

    var listener: Listener? = null

    /** 界面刷新回调 */
    interface Listener {
        /**
         * @param score 当前得分
         * @param matchedGroups 已匹配的组数
         * @param totalGroups 总组数
         */
        fun onGameStateChanged(score: Int, matchedGroups: Int, totalGroups: Int)
    }

    /** 重新开始一局：调用模型的 reset() 重新发牌并清零分数 */
    fun startNewGame() {
        model.reset()
        notifyStateChanged()
    }

    /** 玩家点击了第 index 张牌 */
    fun selectCard(index: Int) {
        if (index < 0 || index >= model.cardCount) {
            return
        }
        model.chooseCardAtIndex(index)
        notifyStateChanged()
    }

    private fun notifyStateChanged() {
        listener?.onGameStateChanged(
            model.score,
            model.matchedGroups,
            model.groupCount
        )
    }

    companion object {
        /** 牌桌上牌的数量（实际由当前屏幕方向对应的资源决定） */
        const val DEFAULT_CARD_COUNT = 24

        /** 每凑够几张牌判定一次，三张牌玩法为 3 */
        const val DEFAULT_MATCH_COUNT = 3
    }
}
