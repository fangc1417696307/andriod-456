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
 * 控制器不直接引用任何 Activity / View，便于单元测试。
 */
class CardGameController(private val cardCount: Int = DEFAULT_CARD_COUNT) {

    var model: CardMatchingGame = CardMatchingGame(cardCount)
        private set

    var listener: Listener? = null

    /** 界面刷新回调 */
    interface Listener {
        /**
         * @param score 当前得分
         * @param matchedPairs 已匹配的牌对数
         * @param totalPairs 总牌对数
         */
        fun onGameStateChanged(score: Int, matchedPairs: Int, totalPairs: Int)
    }

    /** 重新开始一局：重新洗牌并发牌 */
    fun startNewGame() {
        model = CardMatchingGame(cardCount)
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
            model.matchedPairs,
            model.cardCount / 2
        )
    }

    companion object {
        /** 牌桌上牌的数量：4 行 x 4 列 */
        const val DEFAULT_CARD_COUNT = 16
    }
}
