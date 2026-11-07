package cn.edu.sicnu.cardgame.view

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import cn.edu.sicnu.cardgame.R
import cn.edu.sicnu.cardgame.model.CardMatchingGame

/**
 * 牌桌 View（View 层）：用 GridLayout 把 [PlayingCardView] 排成 4 列，
 * 并负责把模型状态渲染到每一张牌上。
 *
 * View 只负责"显示"，不处理任何游戏规则；点击事件通过 [onCardSelected] 抛给控制器。
 */
class CardGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    /** 某张牌被点击时回调，参数为该牌下标 */
    var onCardSelected: ((Int) -> Unit)? = null

    private val cardViews = mutableListOf<PlayingCardView>()

    init {
        columnCount = COLUMNS
        useDefaultMargins = false
        alignmentMode = ALIGN_MARGINS
    }

    /** 按模型渲染牌桌；牌的数量变化时重建子 View */
    fun render(model: CardMatchingGame) {
        ensureCardViews(model.cardCount)
        for (index in 0 until model.cardCount) {
            cardViews[index].bind(model.cardAtIndex(index))
        }
    }

    private fun ensureCardViews(count: Int) {
        if (cardViews.size == count) {
            return
        }
        removeAllViews()
        cardViews.clear()
        rowCount = (count + COLUMNS - 1) / COLUMNS

        val gap = resources.getDimensionPixelSize(R.dimen.card_gap)
        for (index in 0 until count) {
            val cardView = PlayingCardView(context)
            cardView.cardIndex = index
            cardView.setOnClickListener { onCardSelected?.invoke(index) }

            val layoutParams = LayoutParams(
                GridLayout.spec(index / COLUMNS, 1, 1f),
                GridLayout.spec(index % COLUMNS, 1, 1f)
            )
            layoutParams.width = 0
            layoutParams.height = 0
            layoutParams.setMargins(gap, gap, gap, gap)

            addView(cardView, layoutParams)
            cardViews.add(cardView)
        }
    }

    private companion object {
        const val COLUMNS = 4
    }
}
