package cn.edu.sicnu.cardgame.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.edu.sicnu.cardgame.R
import cn.edu.sicnu.cardgame.model.Card
import com.google.android.material.card.MaterialCardView

/**
 * 单张扑克牌的 View（View 层）。
 *
 * 一张牌由背面（深蓝渐变 + 菱形图案）和正面（点数 + 花色）两层组成，
 * 根据 Card 的 isChosen / isMatched 状态切换显示，并给出不同的边框与透明度。
 */
class PlayingCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    /** 该牌在牌桌上的下标，供控制器回调与测试定位使用 */
    var cardIndex: Int = -1

    /** 当前是否显示正面（供 UI 测试断言） */
    var isFaceUp: Boolean = false
        private set

    private val backLayer: View
    private val frontLayer: View
    private val rankText: TextView
    private val suitText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_playing_card, this, true)
        backLayer = findViewById(R.id.cardBack)
        frontLayer = findViewById(R.id.cardFront)
        rankText = findViewById(R.id.tvRank)
        suitText = findViewById(R.id.tvSuit)

        radius = resources.getDimension(R.dimen.corner_card)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
        isClickable = true
        isFocusable = true
        applyNormalStyle()
    }

    /** 根据模型中的牌状态刷新界面 */
    fun bind(card: Card) {
        isFaceUp = card.isChosen || card.isMatched
        backLayer.visibility = if (isFaceUp) View.INVISIBLE else View.VISIBLE
        frontLayer.visibility = if (isFaceUp) View.VISIBLE else View.INVISIBLE

        if (isFaceUp) {
            val suitColor = ContextCompat.getColor(
                context,
                if (card.isRedSuit) R.color.suit_red else R.color.suit_black
            )
            rankText.text = card.rankName
            rankText.setTextColor(suitColor)
            suitText.text = card.suitName
            suitText.setTextColor(suitColor)
            contentDescription = context.getString(
                R.string.card_desc_revealed, cardIndex + 1, card.toString()
            )
        } else {
            contentDescription = context.getString(R.string.card_desc_hidden, cardIndex + 1)
        }

        when {
            card.isMatched -> applyMatchedStyle()
            card.isChosen -> applyChosenStyle()
            else -> applyNormalStyle()
        }
    }

    private fun applyNormalStyle() {
        alpha = 1f
        scaleX = 1f
        scaleY = 1f
        cardElevation = resources.getDimension(R.dimen.card_elevation)
        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_thin)
        strokeColor = ContextCompat.getColor(context, R.color.divider)
        isEnabled = true
        isClickable = true
    }

    private fun applyChosenStyle() {
        alpha = 1f
        scaleX = 1f
        scaleY = 1f
        cardElevation = resources.getDimension(R.dimen.card_elevation_chosen)
        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_selected)
        strokeColor = ContextCompat.getColor(context, R.color.gold)
        isEnabled = true
        isClickable = true
    }

    private fun applyMatchedStyle() {
        alpha = ALPHA_MATCHED
        scaleX = SCALE_MATCHED
        scaleY = SCALE_MATCHED
        cardElevation = 0f
        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_thin)
        strokeColor = ContextCompat.getColor(context, R.color.divider)
        isEnabled = false
        isClickable = false
    }

    private companion object {
        const val ALPHA_MATCHED = 0.55f
        const val SCALE_MATCHED = 0.92f
    }
}
