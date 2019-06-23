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
import android.widget.EditText



class Edit_profile : Fragment() {


    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/edit_profile.php"
    private var email : TextView?= null
    private var first_name : TextView?= null
    private var last_name : TextView?= null
    private var user_id : Int?=null
    private var save_profile : Button?=null
    private var send_email :String?=null
    private var send_first_name :String?=null
    private var send_lastname :String?=null
    private var category_id :Int?=null
    private var listView: ListView?=null
    public var complete_array_size: Int?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        email=view?.findViewById<EditText>(R.id.profile_email)
        first_name=view?.findViewById<EditText>(R.id.profile_name)
        last_name=view?.findViewById<EditText>(R.id.profile_lastname)
        save_profile= view?.findViewById<Button>(R.id.profile_save)
        user_id=6
        listView=view?.findViewById<ListView>(R.id.edit_listview) as ListView


        Fuel.get(url!!, listOf("user_id" to user_id)).responseJson{ request, response, result ->

//            Toast.makeText(activity,result.get().array().getJSONObject(0).getString("first_name").toString(),Toast.LENGTH_LONG).show()
            email?.text = result.get().array().getJSONObject(0).getString("email").toString()
            first_name?.text = result.get().array().getJSONObject(0).getString("first_name").toString()
            last_name?.text = result.get().array().getJSONObject(0).getString("last_name").toString()
            category_id= result.get().array().getJSONObject(0).getInt("category_id")

            Fuel.get(url!!, listOf("edit_personal" to user_id,"user_category" to category_id.toString() )).responseJson{ request, response, result ->

                complete_array_size=result.get().array().length()
//                Toast.makeText(activity,result.get().array().getJSONObject(0).toString(),Toast.LENGTH_LONG).show()

                val names = arrayOfNulls<String>(complete_array_size!!)
                val question_id = arrayOfNulls<Int>(complete_array_size!!)
                val question_answer=arrayOfNulls<String>(complete_array_size!!)

                for (i in 0 until complete_array_size!!) {

                    names[i]=result.get().array().getJSONObject(i).getString("question_name").toString()
                    question_id[i]=result.get().array().getJSONObject(i).getInt("question_id")
                    question_answer[i]=result.get().array().getJSONObject(i).getString("answer").toString()
                }

                listView!!.adapter=MyCustomAdapter(activity!!,complete_array_size!!,names,question_answer)


            }

        }


        save_profile?.setOnClickListener {
            send_email = email?.text.toString()
            send_first_name=first_name?.text.toString()
            send_lastname=last_name?.text.toString()

            Fuel.get(url!!, listOf("edit_profile" to user_id,"email" to send_email , "first_name" to send_first_name , "last_name" to send_lastname
            )).responseJson{ request, response, result ->
                //                //            Toast.makeText(activity,result.get().array().getJSONObject(0).getString("first_name").toString(),Toast.LENGTH_LONG).show()
//
//                Toast.makeText(activity,result.get().content,Toast.LENGTH_LONG).show()

            }


        }

        }

    private class MyCustomAdapter(
        context: Context,
        ArraySize: Int, Ques: Array<String?>,
        Answers: Array<String?>
        ): BaseAdapter() {

        private val mContext: Context
        private val ArraySize: Int
        private val Ques: Array<String?>
        private val Answer: Array<String?>


        init {
            mContext = context
            this.ArraySize=ArraySize
            this.Answer= Answers
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
            ques.setText(Answer.get(position))
            ques.tag = position


            return rowMain


        }

    }


}