package com.example.talentsboard


import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v7.app.AppCompatActivity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import android.view.LayoutInflater
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    private var notifcation_nb : TextView?= null
    private var notification_bool:Boolean?=false
    private var search_icon: ImageButton?= null
    private var logout: ImageView?= null
    private var email: String?= null
    private var user_id: String?= null
    private var notificationManager:NotificationManager? = null
    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/notification.php"

    private val mOnNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.navigation_home -> {

                val bundle = Bundle()
                bundle.putString("Email Address",this.email!!)
                bundle.putString("userId",this.user_id!!)
                val fragment = MainFragment("%",1)
                val transaction = supportFragmentManager.beginTransaction()
                fragment.arguments = bundle
                transaction.replace(R.id.linear_main, fragment)
                transaction.commit()

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_leaderboard -> {

                val bundle = Bundle()
                bundle.putString("Email Address",this.email!!)
                bundle.putString("userId",this.user_id!!)
                val fragment = LeaderboardFragment()
                val transaction = supportFragmentManager?.beginTransaction()
                fragment.arguments = bundle
                transaction?.replace(R.id.linear_main, fragment)
                transaction?.commit()

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {

                val bundle = Bundle()
                bundle.putString("Email Address",this.email!!)
                bundle.putString("userId",this.user_id!!)
                val fragment = notificationFragment()
                val transaction = supportFragmentManager.beginTransaction()
                fragment.arguments = bundle
                transaction.replace(R.id.linear_main, fragment)
                transaction.commit()

                if(notification_bool!!) {
                    val bottomNavigationMenuView = navigation.getChildAt(0) as BottomNavigationMenuView
                    val v = bottomNavigationMenuView.getChildAt(2)
                    val itemView = v as BottomNavigationItemView
                    itemView.removeViewAt(itemView.childCount - 1)
                    notifications_seen(user_id!!.toInt())
                    notification_bool=false
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {

                val bundle = Bundle()
                bundle.putString("Email Address",this.email!!)
                bundle.putString("userId",this.user_id!!)
                val fragment = ProfileFragment()
                val transaction = supportFragmentManager.beginTransaction()
                fragment.arguments = bundle
                transaction.replace(R.id.linear_main, fragment)
                transaction.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_events -> {

                val bundle = Bundle()
                bundle.putString("Email Address",this.email!!)
                bundle.putString("userId",this.user_id!!)
                val fragment = EventsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                fragment.arguments = bundle
                transaction.replace(R.id.linear_main, fragment)
                transaction.commit()

                return@OnNavigationItemSelectedListener true

            }
        }
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Toast.makeText(this@MainActivity,selectedFile.toString(),Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.search_icon = findViewById(R.id.search_icon) as ImageButton
        this.logout = findViewById(R.id.logout) as ImageView
        this.email = if(intent.getStringExtra("Email Address").isNullOrBlank().not()) intent.getStringExtra("Email Address") else "ihussein@gmail.com"
        this.user_id = "6"


//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) // no keyboard pop by default

        uploadButton.setOnClickListener {

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

        }


        val fragment = MainFragment("%",0)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.linear_main, fragment)
        transaction.commit()
        navigation . setOnNavigationItemSelectedListener (mOnNavigationItemSelectedListener)

        this.logout!!.setOnClickListener {
            startActivity(Intent(this@MainActivity,Authentication::class.java))
            finish()
        }

        this.search_icon!!.setOnClickListener {
            val fragment = SearchFragment()
            val transaction = supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.linear_main, fragment)
            transaction?.commit()
        }

        //notification part
        this.notifcation_nb = findViewById(R.id.notificationsbadge)
        this.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var lastnotiid:String?="0"
        val videos = Array(30) { arrayOfNulls<String>(6) }
        var nb_notification:Int=0


            val timer = Timer()
            timer.schedule(object : TimerTask() {

                override fun run() {
                    runOnUiThread {

                        Fuel.get(url!!, listOf("user" to user_id,"last_id" to lastnotiid!!.toString())).responseJson { request, response, result ->
                            var array_size: Int = result.get().array().length()

                            if (array_size > 0){
                                for (i in 0 until array_size!!) {
                                    videos[i][0] = result.get().array().getJSONObject(i).getString("message")
                                    videos[i][1] = result.get().array().getJSONObject(i).getString("first_name")
                                    videos[i][2] = result.get().array().getJSONObject(i).getString("last_name")
                                    videos[i][3] = result.get().array().getJSONObject(i).getString("url")
                                    videos[i][4] = result.get().array().getJSONObject(i).getString("created_date")
                                    videos[i][5] = result.get().array().getJSONObject(i).getString("notification_id")
                                    sendNotification(result.get().array().getJSONObject(i).getString("first_name")+" "+result.get().array().getJSONObject(i).getString("last_name")+" "+result.get().array().getJSONObject(i).getString("message"),result.get().array().getJSONObject(i).getString("notification_id").toInt())
                                    lastnotiid = result.get().array().getJSONObject(i).getString("notification_id")
                                    nb_notification++
                                }
                                showicon(3)
                                notification_bool=true

                            }

                        }

                    }
                }
            }, 0, 5000)
    }


    fun notifications_seen(userid:Int){

        Fuel.get(url!!, listOf("user_seen" to userid)).responseJson { request, response, result ->
                toast(result.get().content)
            }
    }
    fun sendNotification(text:String,notificationId:Int){

        val notification = Notification.Builder(this@MainActivity)
            .setContentTitle("Talentsboard")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        notificationManager?.notify(notificationId, notification)

    }

    fun showicon(pos:Int){
        val bottomNavigationMenuView = navigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(pos)
        val itemView = v as BottomNavigationItemView
        val badge = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.notifications_badge, itemView, true)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}
