package cn.edu.sicnu.cardgame.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.R
import cn.edu.sicnu.cardgame.model.Card

/**
 * 牌桌适配器（Adapter 层）：把模型里的 [Card] 列表渲染成 RecyclerView 的各个格子。
 *
 * 重载了 RecyclerView.Adapter 的三个抽象方法：
 *  - [getItemCount]       一共有多少张牌；
 *  - [onCreateViewHolder] 创建（复用）一张牌的 ViewHolder；
 *  - [onBindViewHolder]   把某一张牌的数据绑定到 ViewHolder 上。
 *
 * "回调"的处理放在 [onCardClick]：牌被点击时把下标回传给控制器，
 * 适配器自己不修改任何游戏状态，保证 MVC 的分层不被打破。
 *
 * @param rows 牌桌的行数，用来把 RecyclerView 的高度平均分给每一行
 * @param onCardClick 某张牌被点击时的回调，参数为该牌的下标
 */
class CardAdapter(
    private val rows: Int,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var cards: List<Card> = emptyList()

    /** 用模型中的牌刷新列表 */
    fun submitCards(cards: List<Card>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false) as PlayingCardView
        // 格子高度 = 牌桌可用高度 / 行数，使整张牌桌刚好铺满，横屏竖屏都保持规整
        applyCellHeight(view, parent)
        val holder = CardViewHolder(view)
        // 点击回调：把"第几张牌"交给控制器，由控制器决定游戏怎么走
        view.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onCardClick(position)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        (holder.itemView.parent as? ViewGroup)?.let { applyCellHeight(holder.itemView, it) }
        holder.bind(cards[position], position)
    }

    private fun applyCellHeight(itemView: android.view.View, parent: ViewGroup) {
        if (rows <= 0) {
            return
        }
        val availableHeight = parent.height - parent.paddingTop - parent.paddingBottom
        val margins = itemView.layoutParams.let { params ->
            if (params is ViewGroup.MarginLayoutParams) {
                params.topMargin + params.bottomMargin
            } else {
                0
            }
        }
        // 每行实际占用"格子高度 + 上下外边距"，因此要把外边距减掉，否则最后一行会被挤出屏幕
        val height = availableHeight / rows - margins
        if (height > 0 && itemView.layoutParams.height != height) {
            itemView.layoutParams.height = height
        }
    }

    /** 单张牌的 ViewHolder，持有 [PlayingCardView] 并把模型状态渲染上去 */
    class CardViewHolder(private val cardView: PlayingCardView) :
        RecyclerView.ViewHolder(cardView) {

        fun bind(card: Card, position: Int) {
            cardView.cardIndex = position
            cardView.bind(card)
        }
    }
}
