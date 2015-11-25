package dbergum.com.points

import com.firebase.client.Firebase

/**
 * Created by David on 11/23/2015.
 */
open class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
    }

}