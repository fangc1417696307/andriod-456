package cn.edu.sicnu.cardgame

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import cn.edu.sicnu.cardgame.data.PlayerStore

/**
 * 应用入口：统一初始化玩家信息，并固定使用浅色模式，
 * 以保证三个界面的配色在各种系统设置下保持一致。
 */
class CardGameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        PlayerStore.init(this)
    }
}
