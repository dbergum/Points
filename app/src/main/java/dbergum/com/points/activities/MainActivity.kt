package dbergum.com.points.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import dbergum.com.points.R
import dbergum.com.points.services.Users
import java.util.*

/**
 * Created by David on 11/23/2015.
 */
class MainActivity  : AppCompatActivity(){
    var email: String? = null
    var uid: String? = null
    var users: Users = Users()
    var user: Users.User? = null
    var usersMap :HashMap<String, Users.User>? = HashMap();
    var listener  = object : ValueEventListener{
        override fun onDataChange(data: DataSnapshot?) {

            val usr = data!!.child(uid)
            user = usr.getValue(Users.User::class.java)

            setMessage("Hi " + user!!.email + " (" + user!!.points +")")

            var dropdown = findViewById(R.id.emails) as AppCompatSpinner

            var adapter = ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_spinner_item)

            for(child in data.children){
                val childUser = child.getValue(Users.User::class.java)
                adapter.add(childUser.email+":"+childUser.points)
                usersMap!!.put(childUser.email,childUser)
            }
            dropdown.adapter = adapter
        }

        override fun onCancelled(error: FirebaseError?) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        email = intent.getStringExtra("email")
        uid = intent.getStringExtra("uid")
        setMessage("Hi " + email)
    }

    override fun onResume() {
        super.onResume()
        users.listen(listener)

    }

    override fun onPause() {
        super.onPause()
        users.stopListening(listener)
    }

    private fun setMessage(message: String){
        var userName: AppCompatTextView = findViewById(R.id.user_name) as AppCompatTextView
        userName.text = message;
    }

    public fun sendPoints(view : View){
        var pointsEditText = findViewById(R.id.points_input) as EditText
        var value = pointsEditText.text.toString()
        //TODO put error text in strings.xml for possible future localization
        if(!TextUtils.isEmpty(value)){
            try{
                var points = Integer.parseInt(value)
                if(user!!.points < points){
                    pointsEditText.error = "You don't have that many points to send."
                }else{
                    var dropdown = findViewById(R.id.emails) as AppCompatSpinner
                    var email = (dropdown.selectedItem as String).split(":")[0]
                    val recipient  = usersMap!!.get(email)
                    recipient!!.points += points
                    user!!.points -= points
                    users.createOrUpdate(user!!)
                    users.createOrUpdate(recipient!!)
                    setMessage("Hi " + user!!.email + " (" + user!!.points +")")

                }
            }catch(e :NumberFormatException){
                pointsEditText.error = "Error parsing points"
            }

        }else{
            pointsEditText.error = "Error please input the number of points you want to send."

        }

    }

}