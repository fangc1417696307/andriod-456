package cn.edu.sicnu.cardgame

import android.os.Bundle
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.controller.CardGameController
import cn.edu.sicnu.cardgame.data.PlayerStore
import cn.edu.sicnu.cardgame.view.CardAdapter
import com.google.android.material.button.MaterialButton

/**
 * 翻牌游戏界面（二级 Activity / Controller 层）。
 *
 * 实验五把原来的 GridLayout 牌桌换成 RecyclerView：
 *  - RecyclerView + GridLayoutManager 负责摆放 24 张牌，列的个数由资源文件决定，
 *    因此竖屏是 4 列 24 张、横屏是 6 列 12 张，同一份代码适配两种方向；
 *  - [CardAdapter] 负责"模型 -> 界面"的渲染，并把点击回调交给控制器；
 *  - 本类只做接线：把点击交给 [CardGameController]，在回调里刷新界面。
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var scoreText: TextView
    private lateinit var groupsText: TextView
    private lateinit var bestText: TextView

    /** 控制器：界面上的所有操作都转交给它，UI 测试也通过它读取模型的牌面 */
    @VisibleForTesting
    internal lateinit var controller: CardGameController

    /** 当前方向下牌桌上的牌数（竖屏 24、横屏 12，定义在 integers.xml / values-land） */
    private val cardCount: Int
        get() = resources.getInteger(R.integer.game_card_count)

    /** 当前方向下牌桌的列数（竖屏 4、横屏 6） */
    private val columnCount: Int
        get() = resources.getInteger(R.integer.game_column_count)

    /** 每凑够几张牌判定一次 */
    private val matchCount: Int
        get() = resources.getInteger(R.integer.game_match_count)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cardsRecyclerView = findViewById(R.id.rvCards)
        scoreText = findViewById(R.id.tvScore)
        groupsText = findViewById(R.id.tvGroups)
        bestText = findViewById(R.id.tvBest)
        findViewById<TextView>(R.id.tvMode).text = getString(R.string.mode_three_cards)

        setupRecyclerView()

        controller = CardGameController(cardCount, matchCount)
        controller.listener = object : CardGameController.Listener {
            override fun onGameStateChanged(score: Int, matchedGroups: Int, totalGroups: Int) {
                if (score > PlayerStore.highScore) {
                    PlayerStore.highScore = score
                }
                adapter.submitCards(controller.model.cards)
                scoreText.text = getString(R.string.score_format, score)
                groupsText.text = getString(R.string.groups_format, matchedGroups, totalGroups)
                bestText.text = getString(R.string.msg_high_score, PlayerStore.highScore)
            }
        }

        findViewById<MaterialButton>(R.id.btnRestart).setOnClickListener {
            controller.startNewGame()
        }
        findViewById<MaterialButton>(R.id.btnExit).setOnClickListener {
            finish()
        }

        // 进入界面即开始新的一局
        controller.startNewGame()
    }

    /**
     * 配置 RecyclerView：列数取自资源，行数由"牌数 / 列数"算出并交给适配器，
     * 适配器据此把牌桌高度平均分给每一行，横屏竖屏都能铺满且不出现空白行。
     */
    private fun setupRecyclerView() {
        val rows = (cardCount + columnCount - 1) / columnCount
        adapter = CardAdapter(rows) { index -> controller.selectCard(index) }
        cardsRecyclerView.layoutManager = GridLayoutManager(this, columnCount)
        cardsRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
