

package com.example.talentsboard


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.w3c.dom.Comment
import java.util.*

@SuppressLint("ValidFragment")

class CommentFragment @SuppressLint("ValidFragment") constructor(user_id:Int,upload:String) : DialogFragment() {

    var user_id:Int?=null
    var upload:String?=null
    var listViewcom: ListView?=null

    var editText:EditText?=null
    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/comments.php"
    init {
        this.user_id=user_id
        this.upload=upload
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        listViewcom = view?.findViewById(R.id.comlist) as ListView
        editText=view?.findViewById(R.id.editText)
        Fuel.get(url!!, listOf("user" to user_id, "comments" to upload)).responseJson { request, response, result ->
            val array_size = result.get().array().length()
            val comments = Array(20){ arrayOfNulls<String>(7)}
            var last_ind=0

            for (i in 0 until array_size!!) {
                comments[i][0] = result.get().array().getJSONObject(i).getString("comment_id")
                comments[i][1] = result.get().array().getJSONObject(i).getString("user_id")
                comments[i][2] = result.get().array().getJSONObject(i).getString("comment")
                comments[i][3] = result.get().array().getJSONObject(i).getString("date")
                comments[i][4] = result.get().array().getJSONObject(i).getString("post_id")
                comments[i][5] = result.get().array().getJSONObject(i).getString("first_name")
                comments[i][6] = result.get().array().getJSONObject(i).getString("last_name")
                last_ind++
            }


            listViewcom!!.adapter =
                MyCustomAdapter(activity!!, array_size!!, comments)


            val user_image = view?.findViewById<ImageView>(R.id.send)
            user_image!!.setOnClickListener {
                scrollMyListViewToBottom()

                comments[last_ind-1][0] = result.get().array().getJSONObject(0).getString("comment_id")
                comments[last_ind-1][1] = result.get().array().getJSONObject(0).getString("user_id")
                comments[last_ind-1][2] = editText!!.text.toString()
                comments[last_ind-1][3] = result.get().array().getJSONObject(0).getString("date")
                comments[last_ind-1][4] = result.get().array().getJSONObject(0).getString("post_id")
                comments[last_ind-1][5] = result.get().array().getJSONObject(0).getString("first_name")
                comments[last_ind-1][6] = result.get().array().getJSONObject(0).getString("last_name")
//                last_ind++
                Fuel.get(url!!, listOf("user" to user_id, "commentstring" to editText!!.text,"upload" to upload
                )).responseJson { request, response, result ->



//                    Toast.makeText(activity,result.get().content, Toast.LENGTH_LONG).show()
                }
//                last_ind++

                listViewcom!!.deferNotifyDataSetChanged()
            }
        }



    }
    public fun scrollMyListViewToBottom() {
        listViewcom!!.post(Runnable {
            // Select the last row so it will scroll into view...
            listViewcom!!.setSelection( listViewcom!!.adapter.count - 1)
        })

    }


    private class MyCustomAdapter(
        context: Context,
        ArraySize: Int, comments: Array<Array<String?>>
    ): BaseAdapter() {

        private val mContext: Context
        private val ArraySize: Int
        private val comments: Array<Array<String?>>


        init {
            mContext = context
            this.ArraySize=ArraySize
            this.comments=comments

        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return ArraySize
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
            val comMain= layoutInflater.inflate(R.layout.comment_inflater, viewGroup,false)
            val comment_name= comMain.findViewById<TextView>(R.id.Comment_name)
//            val comment_text=comMain.findViewById<TextView>(R.id.comment_text)
            comment_name.text=comments[position][5]+" "+comments[position][6]+": "+comments[position][2]
//            comment_text.text=comments[position][2]
            val user_image = comMain.findViewById<ImageView>(R.id.send)
//            user_image!!.setOnClickListener {
//                Toast.makeText(mContext,"hi",Toast.LENGTH_LONG).show()
//                //                    scrollMyListViewToBottoms()
//
//            }


            return comMain

        }
    }

}
