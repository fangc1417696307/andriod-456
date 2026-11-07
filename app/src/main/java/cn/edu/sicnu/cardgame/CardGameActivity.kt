package cn.edu.sicnu.cardgame

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.controller.CardGameController
import cn.edu.sicnu.cardgame.data.PlayerStore
import cn.edu.sicnu.cardgame.view.CardGameView
import com.google.android.material.button.MaterialButton

/**
 * 翻牌游戏界面（二级 Activity）。
 *
 * 本类只做 Controller 的"接线"工作：
 *  - 把 View 的点击事件交给 [CardGameController]；
 *  - 在控制器回调中刷新 View 与得分文本。
 * 游戏规则全部在 Model 中，界面状态全部由 Model 驱动（MVC）。
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var cardGameView: CardGameView
    private lateinit var scoreText: TextView
    private lateinit var pairsText: TextView
    private lateinit var bestText: TextView
    private lateinit var controller: CardGameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cardGameView = findViewById(R.id.cardGameView)
        scoreText = findViewById(R.id.tvScore)
        pairsText = findViewById(R.id.tvPairs)
        bestText = findViewById(R.id.tvBest)

        controller = CardGameController()
        controller.listener = object : CardGameController.Listener {
            override fun onGameStateChanged(score: Int, matchedPairs: Int, totalPairs: Int) {
                if (score > PlayerStore.highScore) {
                    PlayerStore.highScore = score
                }
                cardGameView.render(controller.model)
                scoreText.text = getString(R.string.score_format, score)
                pairsText.text = getString(R.string.pairs_format, matchedPairs, totalPairs)
                bestText.text = getString(R.string.msg_high_score, PlayerStore.highScore)
            }
        }
        cardGameView.onCardSelected = { index -> controller.selectCard(index) }

        findViewById<MaterialButton>(R.id.btnRestart).setOnClickListener {
            controller.startNewGame()
        }
        findViewById<MaterialButton>(R.id.btnExit).setOnClickListener {
            finish()
        }

        // 进入界面即开始新的一局
        controller.startNewGame()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
