package com.example.talentsboard


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import android.support.v4.app.FragmentManager
import java.util.*


@SuppressLint("ValidFragment")
class MainFragment constructor(type :String,timer:Int) : Fragment()  {

    var type:String?=null
    var timer:Int?=null
    init {
        this.type=type
        this.timer=timer
    }

    var user_id: Int? = 6
    var url: String = "http://10.0.2.2/TalentsboardAndroidsqlConnect/homepage.php"
    private lateinit var listView: ListView

    val users_id = ArrayList<String>()
    val first_name = ArrayList<String>()
    val last_name = ArrayList<String>()
    val liked_video = ArrayList<String>()
    val nb_likes = ArrayList<String>()
    val nb_commetns = ArrayList<String>()
    val upload_date= ArrayList<String>()
    val profile_photo = ArrayList<String>()
    val video_url = ArrayList<String>()
    val upload_id = ArrayList<String>()
    val tags = ArrayList<String>()
    val timerz = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val img_all = view?.findViewById<ImageView>(R.id.img_all)
        val img_sports = view?.findViewById<ImageView>(R.id.img_sports)
        val img_dance = view?.findViewById<ImageView>(R.id.img_dancing)
        val img_singing = view?.findViewById<ImageView>(R.id.img_singing)

        user_id = 6
        listView = view?.findViewById(R.id.homepage) as ListView
        var spin_sport=view?.findViewById<Spinner>(R.id.spin_sports)
        var spin_dance=view?.findViewById<Spinner>(R.id.spin_dance)
        var spin_sing=view?.findViewById<Spinner>(R.id.spin_sing)

        val aa = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.sports))
        val bb = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item,resources.getStringArray(R.array.dance))
        val cc = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item,resources.getStringArray(R.array.sing))
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spin_sport!!.adapter = aa
        spin_dance!!.adapter = bb
        spin_sing!!.adapter = cc

        spin_sport?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position==1){
                            val fragment = MainFragment("6",1)
                            val transaction = fragmentManager!!.beginTransaction()
                            transaction.replace(R.id.linear_main, fragment)
                            transaction.commit()
                        }
            }
        }

        img_all!!.setOnClickListener{

            val fragment = MainFragment("%",1)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.linear_main, fragment)
            transaction.commit()


        }


        img_sports!!.setOnClickListener{

            val fragment = MainFragment("4",1)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.linear_main, fragment)
            transaction.commit()

        }
        img_dance!!.setOnClickListener{

            val fragment = MainFragment("2",1)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.linear_main, fragment)
            transaction.commit()

        }
        img_singing!!.setOnClickListener{

            val fragment = MainFragment("8",1)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.linear_main, fragment)
            transaction.commit()

        }



        val fm =this@MainFragment.fragmentManager
        var array_size=0
        var array_sizez=0

        var last_vid_id="0"
        Fuel.get(url!!, listOf("user" to user_id,"filter" to type)).responseJson { request, response, result ->


            array_size = result.get().array().length()


            try{
                for (i in 0 until array_size!!) {
                    users_id.add(result.get().array().getJSONObject(i).getString("user_id"))
                    first_name.add(result.get().array().getJSONObject(i).getString("first_name"))
                    last_name.add(result.get().array().getJSONObject(i).getString("last_name"))
                    liked_video.add(result.get().array().getJSONObject(i).getString("liked_video"))
                    nb_commetns.add(result.get().array().getJSONObject(i).getString("nb_comments"))
                    nb_likes.add(result.get().array().getJSONObject(i).getString("nb_likes"))
                    upload_date.add(result.get().array().getJSONObject(i).getString("upload_date"))
                    profile_photo.add(result.get().array().getJSONObject(i).getString("profile_photo"))
                    video_url.add(result.get().array().getJSONObject(i).getString("video_url"))
                    upload_id.add(result.get().array().getJSONObject(i).getString("upload_id"))
                    tags.add(result.get().array().getJSONObject(i).getString("tags"))
                }
            } catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }

            last_vid_id=result.get().array().getJSONObject(array_size-1).getString("upload_id")


            var  adapter =
                MyCustomAdapter(
                    activity!!, array_size!!, users_id, first_name, last_name, nb_likes, nb_commetns,
                    liked_video, tags, video_url, upload_id, profile_photo, upload_date, type!!, fm!!
                )

            listView!!.adapter = adapter


                timerz.schedule(
                    object : TimerTask() {
                    override fun run() {
                        activity!!.runOnUiThread {

                            Fuel.get(url!!, listOf("user" to user_id, "last_vid_id" to last_vid_id))
                                .responseJson { request, response, result ->
                                    array_sizez = result.get().array().length()
                                    if (array_sizez > 0) {
                                        try{
                                            for (i in 0 until array_sizez!!) {
                                                users_id.add(result.get().array().getJSONObject(i).getString("user_id"))
                                                first_name.add(result.get().array().getJSONObject(i).getString("first_name"))
                                                last_name.add(result.get().array().getJSONObject(i).getString("last_name"))
                                                liked_video.add(result.get().array().getJSONObject(i).getString("liked_video"))
                                                nb_commetns.add(result.get().array().getJSONObject(i).getString("nb_comments"))
                                                nb_likes.add(result.get().array().getJSONObject(i).getString("nb_likes"))
                                                upload_date.add(result.get().array().getJSONObject(i).getString("upload_date"))
                                                profile_photo.add(result.get().array().getJSONObject(i).getString("profile_photo"))
                                                video_url.add(result.get().array().getJSONObject(i).getString("video_url"))
                                                upload_id.add(result.get().array().getJSONObject(i).getString("upload_id"))
                                                tags.add(result.get().array().getJSONObject(i).getString("tags"))
                                            }
                                        } catch (e: Exception){
                                            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
                                        }
                                        last_vid_id =
                                            result.get().array().getJSONObject(array_sizez - 1).getString("upload_id")
                                        adapter.notifyDataSetChanged()
                                        listView.invalidateViews()

                                    }

                                }
                        }
                    }

                }, 0, 7000)

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        timerz.cancel()
    }

    private class MyCustomAdapter(
        context: Context,
        ArraySize: Int,users_id : ArrayList<String>,
        first_name: ArrayList<String>,
        last_name: ArrayList<String>,
        nb_likes: ArrayList<String>,
        nb_commetns: ArrayList<String>,liked_video: ArrayList<String>,
        tags: ArrayList<String>,
        url: ArrayList<String>,upload_id: ArrayList<String>,
        profile_photo: ArrayList<String>,upload_date: ArrayList<String>,
        type:String,
        FragmentManager: FragmentManager
    ) : BaseAdapter() {

        var FragmentManager: FragmentManager? = null
        private val mContext: Context
        private val ArraySize: Int
        private val type:String
        private val users_id: ArrayList<String>
        private val first_name: ArrayList<String>
        private val last_name: ArrayList<String>
        private val nb_likes: ArrayList<String>
        private val liked_video: ArrayList<String>
        private val nb_commetns: ArrayList<String>
        private val url: ArrayList<String>
        private val tags: ArrayList<String>
        private val upload_date: ArrayList<String>
        private val upload_id: ArrayList<String>
        private val profile_photo: ArrayList<String>


        init {
            this.FragmentManager= FragmentManager
            mContext = context
            this.ArraySize = ArraySize
            this.users_id=users_id
            this.type=type
            this.first_name=first_name
            this.last_name=last_name
            this.nb_commetns=nb_commetns
            this.nb_likes=nb_likes
            this.liked_video=liked_video
            this.url=url
            this.tags=tags
            this.upload_date=upload_date
            this.profile_photo=profile_photo
            this.upload_id=upload_id
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return users_id.size
        }
        // you can also ignore this
        override fun getItemId(position: Int): Long {

            return position.toLong()
        }

        // you can ignore this for now
        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        // responsible for rendering out each row
        @SuppressLint("ResourceType")
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {


            val layoutInflater = LayoutInflater.from(mContext)

             val  rowMain = layoutInflater.inflate(R.layout.homepage_inflater, viewGroup, false)

                val obj = MainFragment(type,1)
                val first_names = rowMain.findViewById<TextView>(R.id.home_name)
                val nb_likess = rowMain.findViewById<TextView>(R.id.nb_likes)
                val nb_commentss = rowMain.findViewById<TextView>(R.id.nb_comments)
                val tagss = rowMain.findViewById<TextView>(R.id.tags)
                val date = rowMain.findViewById<TextView>(R.id.date)
                val user_image = rowMain.findViewById<ImageView>(R.id.imageView5)
                val red_like = rowMain.findViewById<ImageView>(R.id.red_like)
                val button = rowMain.findViewById<Button>(R.id.like_button)
                 val commentbutton = rowMain.findViewById<Button>(R.id.commentbutton)
                val photo = profile_photo[users_id.size-position-1]


             //videos------------------------------------------------------------------------
                val imageres =
                    rowMain.resources.getIdentifier("drawable/" + photo!!.split(".")[0], null, mContext.packageName)
                user_image.setImageResource(imageres)
                user_image.tag=users_id[users_id.size-position-1]
                user_image.setOnClickListener {

                    val fragment = userprofileFragment(user_image.tag.toString())
                    val transaction = FragmentManager!!.beginTransaction()
                    transaction.replace(R.id.linear_main, fragment)
                    transaction.commit()

                }

                val WebView = rowMain.findViewById<WebView>(R.id.home_video)
                val videoStr =
                    "<html><body><iframe width=383 height=212" +
                            " src=https://www.youtube.com/embed/" + url[users_id.size-position-1] + " frameborder=0 allowfullscreen></iframe></body></html>"
                WebView!!.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return false
                    }
                }

                val ws = WebView!!.settings
                ws.javaScriptEnabled = true
                WebView!!.loadData(videoStr, "text/html", "utf-8")

                first_names.text =first_name[users_id.size-position-1]  + " " + last_name[users_id.size-position-1]
                nb_likess.text = nb_likes[users_id.size-position-1]
                nb_commentss.text = nb_commetns[users_id.size-position-1]
                tagss.text = tags[users_id.size-position-1]
                date.text = upload_date[users_id.size-position-1]
                button.tag = upload_id[users_id.size-position-1]
                button.id=0
                commentbutton.tag= upload_id[users_id.size-position-1]
                    //likes---------------------------------------------------------------------------------------------
                if(liked_video[users_id.size-position-1]!="null"){
                    button.id=1
                    button.setTextColor(Color.RED)
                    red_like.setImageResource(R.drawable.red_like)
                }

                button.setOnClickListener {
                    if (button.id==0) {

                        Fuel.get(obj.url!!, listOf("user" to obj.user_id, "like" to button.tag.toString()))
                            .responseJson { request, response, result ->
                                //                        Toast.makeText(mContext, result.get().content, Toast.LENGTH_LONG).show()
                            }
                        button.setTextColor(Color.RED)
                        red_like.setImageResource(R.drawable.red_like)
                        button.id=1
                    }
                    else if(button.id==1){

                        Fuel.get(obj.url!!, listOf("user" to obj.user_id, "unlike" to button.tag.toString()))
                        .responseJson { request, response, result ->
                            //                        Toast.makeText(mContext, result.get().content, Toast.LENGTH_LONG).show()
                        }
                        button.setTextColor(Color.BLACK)
                        red_like.setImageResource(R.drawable.liked_videos)
                        button.id=0

                    }

                }

 //           Comments----------------------------------------------------------------------
            commentbutton.setOnClickListener {
                val fragments = CommentFragment(obj.user_id!!,upload_id[users_id.size-position-1])
                fragments.show(FragmentManager,"hello")
            }
                return rowMain

        }
    }
}


