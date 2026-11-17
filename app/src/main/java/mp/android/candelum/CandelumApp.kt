package mp.android.candelum

import android.app.Application

class CandelumApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
        container.home.connect()
    }
}