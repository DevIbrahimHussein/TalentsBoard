package com.example.talentsboard


import android.annotation.SuppressLint
import android.content.Context
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


@SuppressLint("ValidFragment")
class Videos_library_Fragment constructor(type :String) : Fragment() {
    private var type:String?=null
    init {
        this.type=type
    }
    private var listView: ListView?=null
    private var user_id :Int?=null
    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/UserLibrary.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos_library, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        user_id=6


        Fuel.get(url!!, listOf(type!! to user_id)).responseJson{ request, response, result ->

//            first_name!!.text=result.get().array().getJSONObject(0).getString("first_name").toString()

//            Toast.makeText(activity,result.get().array().length().toString(),Toast.LENGTH_LONG).show()

            val videos = arrayOfNulls<String>(result.get().array().length()!!)
            for (i in 0 until result.get().array().length()!!) {

                videos[i]=result.get().array().getJSONObject(i).getString("video_url").toString()
            }

            listView=view?.findViewById(R.id.my_video_list) as ListView

            listView!!.adapter= Videos_library_Fragment.MyCustomAdapter(activity!!,videos)


        }

    }

    private class MyCustomAdapter(
        context: Context,
        videos: Array<String?>

    ): BaseAdapter() {

        private val mContext: Context
        private val videos :Array<String?>

        init {
            mContext = context
            this.videos=videos
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return videos.size
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

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater=LayoutInflater.from(mContext)
            val rowMain= layoutInflater.inflate(R.layout.youtube_player_inflater, viewGroup,false)
            val WebView= rowMain.findViewById<WebView>(R.id.videoWebView)
            val videoStr =
                "<html><body><iframe width=383 height=377" +
                        " src=https://www.youtube.com/embed/"+videos[position]+" frameborder=0 allowfullscreen></iframe></body></html>"
            WebView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            val ws = WebView!!.settings
            ws.javaScriptEnabled = true
            WebView!!.loadData(videoStr, "text/html", "utf-8")

            return rowMain

        }

    }

}

