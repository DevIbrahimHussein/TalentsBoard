package com.example.talentsboard


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson


class Complete_profile : Fragment() {

    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/edit_profile.php"
    private var listView: ListView?=null
    public var complete_array_size: Int?=null
    private var user_id : Int?=null
    private var category_id :Int?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView=view?.findViewById<ListView>(R.id.complete_profile_list) as ListView
        user_id=6

        Fuel.get(url!!, listOf("user_id" to user_id)).responseJson { request, response, result ->

            category_id = result.get().array().getJSONObject(0).getInt("category_id")


            Fuel.get(url!!, listOf("complete_personal" to user_id,"user_category" to category_id.toString() )).responseJson{ request, response, result ->

                complete_array_size=result.get().array().length()
//                Toast.makeText(activity,result.get().array().getJSONObject(0).toString(), Toast.LENGTH_LONG).show()

                val names = arrayOfNulls<String>(complete_array_size!!)
                val question_id = arrayOfNulls<Int>(complete_array_size!!)

                for (i in 0 until complete_array_size!!) {

                    names[i]=result.get().array().getJSONObject(i).getString("question_name").toString()
                    question_id[i]=result.get().array().getJSONObject(i).getInt("question_id")
                }

                listView!!.adapter=
                    Complete_profile.MyCustomAdapter(activity!!, complete_array_size!!, names)

            }



        }


    }


    private class MyCustomAdapter(
        context: Context,
        ArraySize: Int, Ques: Array<String?>
    ): BaseAdapter() {

        private val mContext: Context
        private val ArraySize: Int
        private val Ques: Array<String?>


        init {
            mContext = context
            this.ArraySize=ArraySize
            this.Ques=Ques
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
            val rowMain= layoutInflater.inflate(R.layout.complete_pro_inflater, viewGroup,false)
            val ques_name= rowMain.findViewById<TextView>(R.id.complete_q_name)
            val ques=rowMain.findViewById<EditText>(R.id.complete_ques)
            ques_name.text=Ques.get(position)+":"

            return rowMain


        }

    }


}