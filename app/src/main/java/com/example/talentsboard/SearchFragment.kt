package com.example.talentsboard

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_row.view.*
import java.lang.Exception

class SearchFragment : Fragment() {

    private fun network(input: CharSequence){

        try{
            Fuel.get("http://10.0.2.2/Android/TalentsBoard/search.php?input=$input")
                .responseJson { _, _, result ->

                    val size: Int = result.get().array().length() // size of response array
                    var eventSize : Int
                    val userFirstName = arrayOfNulls<String>(size)
                    val userLastName = arrayOfNulls<String>(size)
                    val userEmailAddress = arrayOfNulls<String>(size)
                    val userId = arrayOfNulls<String>(size)

                    val eventName = arrayOfNulls<String>(size)
                    val eventUserId = arrayOfNulls<String>(size)
                    val eventUserEmail = arrayOfNulls<String>(size)

                   if(size != 0){

                       var j = 0
                       try {

                           for(i in 0 until size){

                               if(result.get().array().getJSONObject(i).getString("type").toLowerCase()=="users"){

                                   userFirstName[i] = result.get().array().getJSONObject(i).getString("firstName")
                                   userLastName[i] = result.get().array().getJSONObject(i).getString("lastName")
                                   userEmailAddress[i] = result.get().array().getJSONObject(i).getString("email")
                                   userId[i] = result.get().array().getJSONObject(i).getString("userId")

                               } else {
                                   j = i; break
                               }
                           }

                           eventSize = size - j

                           if(result.get().array().getJSONObject(j).getString("type").toLowerCase()=="events"){
                               for(i in 0 until eventSize){

                                   eventName[i] = result.get().array().getJSONObject(j).getString("eventName")
                                   eventUserId[i] = result.get().array().getJSONObject(j).getString("userId")
                                   eventUserEmail[i] = result.get().array().getJSONObject(j).getString("userEmail")
                                   j++
                               }
                           }

                       } catch (e: Exception){
                           Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
                       }

                   }


                    recycler_view.layoutManager = LinearLayoutManager(context)
                    recycler_view.adapter = MyAdapter(context!!,size,userFirstName,userLastName,userEmailAddress,userId)
                    // here is the adapter
                }
        } catch (e: Exception){
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        network("a")


        search_bar!!.addTextChangedListener( object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                network(p0!!)

            }
        })

    } // end of main

    class MyAdapter(
        mContext: Context,
        size: Int,
        userFirstName: Array<String?>,
        userLastName : Array<String?>,
        userEmailAddress : Array<String?>,
        userId: Array<String?>

    ): RecyclerView.Adapter<CustomViewHolder>() {

        private val mContext: Context
        private var size: Int?= null

        private var userFirstName: Array<String?> ?= null
        private var userLastName: Array<String?> ?= null
        private var userEmailAddress: Array<String?> ?= null
        private var userId: Array<String?> ?= null

        init {

            this.mContext = mContext
            this.size = size
            this.userFirstName = userFirstName
            this.userLastName = userLastName
            this.userEmailAddress = userEmailAddress
            this.userId = userId

        }
        override fun getItemCount(): Int {
            return size!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): CustomViewHolder{

            val layoutInflater = LayoutInflater.from(parent.context)
            val row = layoutInflater.inflate(R.layout.search_row,parent,false)
            return CustomViewHolder(row)

        }

        override fun onBindViewHolder(holder: CustomViewHolder, p1: Int) {
            holder.view.userNameSearch?.text = "${userFirstName!![0]} ${userLastName!![0]}"
        }

    } // end of user adapter class

    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){

        private var userImageView: ImageView?= null
        private var userName: TextView?= null

        init {
            userImageView = view.findViewById(R.id.userImageView) as ImageView
            userName = view.findViewById(R.id.userNameSearch) as TextView
        }

    }

} // end of outer class
