package com.example.talentsboard


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson

class notificationFragment : Fragment() {


    var user_id=6
    private var listView: ListView?=null
    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/notification.php"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView = view?.findViewById(R.id.notification_list) as ListView


            Fuel.get(url!!, listOf("get_all" to user_id)).responseJson { request, response, result ->
            var array_size: Int = result.get().array().length()
                val All_noti = Array(array_size!!) { arrayOfNulls<String>(7) }


                for (i in 0 until array_size!!) {
                    All_noti[i][0] = result.get().array().getJSONObject(i).getString("message")
                    All_noti[i][1] = result.get().array().getJSONObject(i).getString("first_name")
                    All_noti[i][2] = result.get().array().getJSONObject(i).getString("last_name")
                    All_noti[i][3] = result.get().array().getJSONObject(i).getString("url")
                    All_noti[i][4] = result.get().array().getJSONObject(i).getString("created_date")
                    All_noti[i][5] = result.get().array().getJSONObject(i).getString("notification_id")
                    All_noti[i][6] = result.get().array().getJSONObject(i).getString("profile_id")
                }

                listView!!.adapter=
                    MyCustomAdapter(activity!!,array_size!!, All_noti)

        }
    }


    private class MyCustomAdapter(
        context: Context,
        ArraySize: Int, noti: Array<Array<String?>>
    ) : BaseAdapter() {

        private val mContext: Context
        private val ArraySize: Int
        private val noti: Array<Array<String?>>

        init {
            mContext = context
            this.ArraySize = ArraySize
            this.noti = noti
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
        @SuppressLint("ResourceType")
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {



            val layoutInflater = LayoutInflater.from(mContext)

            val  rowMain = layoutInflater.inflate(R.layout.notification_inflater, viewGroup, false)

            val name = rowMain.findViewById<TextView>(R.id.noti_name)
            val text = rowMain.findViewById<TextView>(R.id.noti_text)
            val date = rowMain.findViewById<TextView>(R.id.noti_date)
            val user_image = rowMain.findViewById<ImageView>(R.id.noti_image)
            name.text=noti[position][1]+" "+noti[position][2]
            text.text=noti[position][0]
            date.text=noti[position][4]
            val photo =noti[position][6
            ]

            val imageres =
                rowMain.resources.getIdentifier("drawable/" + photo!!.split(".")[0], null, mContext.packageName)
            user_image.setImageResource(imageres)


            return rowMain

        }
    }


}
