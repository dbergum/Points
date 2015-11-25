package dbergum.com.points.services

import android.util.Log
import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import java.util.concurrent.TimeUnit

/**
 * Created by David on 11/23/2015.
 */
class Users : Service(){

    private final val USERS: String = "users"
    data class User(val email: String, var points: Int, var uid: String){
        public constructor(): this("",0,""){

        }
    }

    public fun listen(listener: ValueEventListener){
        firebase.child(USERS).addValueEventListener(listener)

    }

    public fun stopListening(listener: ValueEventListener){
        firebase.child(USERS).removeEventListener(listener)

    }

    public fun auth(email: String, password: String, handler: Firebase.AuthResultHandler){
        firebase.authWithPassword(email,password,object : Firebase.AuthResultHandler{
            override fun onAuthenticationError(error: FirebaseError?) {
                firebase.createUser(email,password,object:Firebase.ResultHandler{
                    override fun onSuccess() {

                        firebase.authWithPassword(email,password,object : Firebase.AuthResultHandler{
                            override fun onAuthenticationError(p0: FirebaseError?) {
                                handler.onAuthenticationError(p0)
                            }

                            override fun onAuthenticated(p0: AuthData?) {
                                var user = Users.User(email,100,p0!!.uid);
                                createOrUpdate(user)
                                Log.i("Users", "creating user")
                                handler.onAuthenticated(p0)
                            }
                        })
                    }

                    override fun onError(error: FirebaseError?) {
                        handler.onAuthenticationError(error)
                    }
                } )
            }

            override fun onAuthenticated(data: AuthData?) {
                handler.onAuthenticated(data)
            }

        });
    }

    public fun createOrUpdate(user: User){
        firebase.child(USERS).child(user.uid).setValue(user);
    }

}