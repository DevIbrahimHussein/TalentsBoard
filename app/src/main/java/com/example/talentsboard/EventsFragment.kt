package com.example.talentsboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.transition.FragmentTransitionSupport
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.SupportActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.fragment_events.*
import java.lang.Exception


@SuppressLint("ValidFragment")
class EventsFragment: Fragment() {

    private var list: ListView?= null
    private var fab: View?= null
    private var email: String?= null
    private var userId: String?= null
    private var categorySpinner: Spinner?= null
    private var categorySelected: String?= null
    private var categories: Array<String>?= null
    private var r: Runnable?= null
    private var Handler= Handler()
    private var progRunnable: Runnable?= null

    fun network(url: String){

        Fuel.get(url).responseJson { _, _, result ->

            val size: Int = result.get().array().length() // size of response array
            val eventId = arrayOfNulls<String>(size)
            val title = arrayOfNulls<String>(size)
            val holder = arrayOfNulls<String>(size)
            val fullName = arrayOfNulls<String>(size)
            val location = arrayOfNulls<String>(size)
            val description = arrayOfNulls<String>(size)
            val date = arrayOfNulls<String>(size)
            val category = arrayOfNulls<String>(size)
            val attend = arrayOfNulls<Boolean>(size)
            val maxAttenders = arrayOfNulls<String>(size)
            try {
                for (i in 0 until size) { // get all data from server
                    eventId[i] = result.get().array().getJSONObject(i).getString("eventId")
                    title[i] = result.get().array().getJSONObject(i).getString("eventTitle")
                    holder[i] = result.get().array().getJSONObject(i).getString("eventHolder")
                    location[i] = result.get().array().getJSONObject(i).getString("eventLocation")
                    description[i] = result.get().array().getJSONObject(i).getString("eventDescription")
                    date[i] = result.get().array().getJSONObject(i).getString("eventDate")
                    fullName[i] =
                        result.get().array().getJSONObject(i).getString("firstName") + " " + result.get().array().getJSONObject(i).getString("lastName")
                    category[i] = result.get().array().getJSONObject(i).getString("eventCategory")
                    attend[i] = result.get().array().getJSONObject(i).getBoolean("attendEvent")
                    maxAttenders[i] = result.get().array().getJSONObject(i).getString("maxAttenders")
                }
            }catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }
            // send data through adapter's constructor
            if(size==0){
                delay(createEventRes,1000,View.VISIBLE,"NO USERS FOUND") // show successful row after 1 second
                delay(createEventRes,5000,View.GONE,"") // hide successful row after 7 seconds
            }

            r = Runnable {
                this.list?.adapter = Adapter(context!!,eventId,size,holder,title,location,description,date,fullName,attend,createEventResText,createEventRes,list!!,category,userId,maxAttenders!!)
            }

            Handler.postDelayed(r, 1000)
            progressBardelay(progress_bar,1000,View.GONE)

        }

    }

    private fun progressBardelay(v: View, t: Long,action: Int){
        progRunnable = Runnable{
            progress_bar.visibility = action
        }
        progress_bar.postDelayed(progRunnable,t)
    }

    private fun delay(v: View, t: Long,action: Int,msg: String){
        createEventRes.postDelayed( {
            v.visibility = action
            createEventResText.text = msg
        },t)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.fab = view?.findViewById(R.id.fab) as FloatingActionButton
        this.list = view?.findViewById<View>(R.id.event_list_view) as ListView
        this.email = if(this.arguments!!.getString("Email Address").isNullOrBlank().not()) this.arguments!!.getString("Email Address") else "ihussein@gmail.com"
        this.userId = "6"
        this.categorySpinner = view?.findViewById(R.id.spin)as Spinner
        this.categories = arrayOf("All","Football", "BasketBall", "Boxing", "Sports", "Dancing", "Acting","Comedy","Singing")

        this.categorySpinner?.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1,
            this.categories
        )

        this.categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
                network("http://10.0.2.2/Android/TalentsBoard/retreiveEvents.php?user_id=${userId!!.toInt()}&Category=")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categorySelected = categories!![p2]
                network("http://10.0.2.2/Android/TalentsBoard/retreiveEvents.php?user_id=${userId!!.toInt()}&Category=${categorySelected.toString()}")
            }

        }//end of spinner

        network("http://10.0.2.2/Android/TalentsBoard/retreiveEvents.php?user_id=${userId!!.toInt()}&Category=")

        if(!this.arguments!!.getString("Event Created").isNullOrEmpty()){ // data receive when successfully create event from create event page
            delay(createEventRes,1000,View.VISIBLE,this.arguments!!.getString("Event Created")!!) // show successful row after 1 second
            delay(createEventRes,7000,View.GONE,this.arguments!!.getString("Event Created")!!) // hide successful row after 7 seconds
        }

        this.fab?.setOnClickListener {
            val bundle = Bundle() // Data bundle to transfer from fragment/Activity to fragment
            bundle.putString("Email Address",this.email!!)
            bundle.putString("user Id",this.userId!!)
            val fragment = CreateEventFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            fragment.arguments = bundle
            transaction?.replace(R.id.linear_main, fragment)
            transaction?.commit() // go to create event page
        } // end of click listener
    }

    private class Adapter(
        context:Context,
        eventId: Array<String?>,
        size: Int,
        holder: Array<String?>,
        title: Array<String?>,
        location: Array<String?>,
        description: Array<String?>,
        date: Array<String?>,
        name: Array<String?>,
        attend: Array<Boolean?>,
        resText: TextView,
        createEventRes: View,
        list: ListView,
        category: Array<String?>,
        userId: String?,
        maxAttenders: Array<String?>
    ): BaseAdapter() {

        private val layoutInflater: LayoutInflater
        private val mContext: Context
        private var row: View?= null
        private var size: Int?= null
        private var holder: Array<String?>?= null
        private var title: Array<String?>?= null
        private var location: Array<String?>?= null
        private var description: Array<String?>?= null
        private var date: Array<String?>?= null
        private var name: Array<String?>?= null
        private var eventId: Array<String?>?= null
        private var attend: Array<Boolean?>?= null
        private var resText: TextView?= null
        private var createEventRes: View?= null
        private var list: ListView?= null
        private var category: Array<String?>?= null
        private var userId: String?=null
        private var maxAttenders: Array<String?>?= null

        init {

            this.mContext = context
            this.layoutInflater = LayoutInflater.from(this.mContext)
            this.size = size
            this.eventId = eventId
            this.name = name
            this.title = title
            this.holder = holder
            this.location = location
            this.description = description
            this.date = date
            this.attend = attend
            this.resText = resText
            this.createEventRes = createEventRes
            this.list = list
            this.category = category
            this.userId = userId
            this.maxAttenders = maxAttenders
        }

        fun delay(t: Long,action: Int,message: String){
            this.createEventRes!!.postDelayed( {
                this.createEventRes!!.visibility = action
                resText!!.text =  message
            },t)
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return this.size!!
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

            this.row = this.layoutInflater.inflate(R.layout.event_row, viewGroup,false)
            val holderTV = row?.findViewById<View>(R.id.holdertextview) as TextView
            val titleTV = row?.findViewById<View>(R.id.titletextview) as TextView
            val descriptionTV = row?.findViewById<View>(R.id.descriptiontextview) as TextView
            val locationTV = row?.findViewById<View>(R.id.locationtextview) as TextView
            val dateTV = row?.findViewById<View>(R.id.datetextview) as TextView
            val button = this.row?.findViewById<View>(R.id.attendbutton) as TextView
            val category = row?.findViewById<View>(R.id.categoryText) as TextView
            val userId: String? = this.holder!![position]

            holderTV.text = this.name!![position]
            titleTV.text = this.title!![position]
            descriptionTV.text = this.description!![position]
            locationTV.text = this.location!![position]
            dateTV.text = this.date!![position]
            category.text = this.category!![position]

            if (this.attend!![position]!!){ // if attend is true so it will be attended
                button.text = "Attended"
                button.setOnClickListener {
                    // show dialog to authenticate user
                    val alertDialogBuilder = AlertDialog.Builder(mContext,R.style.MyDialogTheme)
                    alertDialogBuilder.setTitle("Alert!!")
                    alertDialogBuilder.setMessage("Are you sure you want to remove attending this event?")
                    alertDialogBuilder.setPositiveButton("YES") { _, _ -> // start networking if yes button clicked to remove attending event
                        Fuel.get("http://10.0.2.2/Android/TalentsBoard/modifyEventAttender.php?" +
                                "removeAttenderEventId&" +
                                "eventId=${eventId!![position]}&" +
                                "userId=${userId!!.trim().toInt()}")
                            .responseJson { _, _, result ->

                                button.text = "Attend"
                                this.createEventRes!!.visibility = View.VISIBLE
                                resText!!.text =  "Successfully removing"
                                delay(2000,View.GONE,"")

                            }
                    }

                    alertDialogBuilder.setNegativeButton("No"){ _, _ -> }
                    alertDialogBuilder.setNeutralButton("Cancel"){ _, _ -> }
                    val dialog: AlertDialog = alertDialogBuilder.create()
                    dialog.show()

                } // end of set on click listener {if}

            } else {
                button.text = "Attend"
                button.setOnClickListener {

                    val alertDialogBuilder = AlertDialog.Builder(mContext,R.style.MyDialogTheme)
                    alertDialogBuilder.setTitle("Alert!!")
                    alertDialogBuilder.setMessage("Are you sure you want to attend this event?")
                    alertDialogBuilder.setPositiveButton("YES") { _, _ -> // start request if yes button clicked to attend event
                        Fuel.get("http://10.0.2.2/Android/TalentsBoard/modifyEventAttender.php?" +
                                "addAttenderEventId&" +
                                "eventId=${eventId!![position]}&" +
                                "userId=${this.userId!!.trim().toInt()}")
                            .responseJson { _, _, result ->
                                button.text = "Attended"
                                this.createEventRes!!.visibility = View.VISIBLE
                                resText!!.text =  "Successfully attending this event"
                                delay(2000,View.GONE,"")

                            } // end of response
                    }
                    alertDialogBuilder.setNegativeButton("No"){ _, _ -> }
                    alertDialogBuilder.setNeutralButton("Cancel"){ _, _ -> }
                    val dialog: AlertDialog = alertDialogBuilder.create()
                    dialog.show()
                } // end of dialog and networking work

            } // end of click listener {else}
            return this.row!!

        } // end of row rendering

    } // end of main

} // end of outer class
