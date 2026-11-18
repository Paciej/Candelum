package mp.android.candelum

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.core.os.postDelayed
import mp.android.candelum.light.LightCommand
import mp.android.candelum.light.LightTarget
import java.util.logging.Handler

class CandelumApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
        container.home.connect()
    }
}