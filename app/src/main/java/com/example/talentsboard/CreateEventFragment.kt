package com.example.talentsboard

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.fragment_events.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ValidFragment")
class CreateEventFragment: Fragment() {
    // private data types
    private var eventName: EditText?= null
    private var eventLocation: EditText?= null
    private var eventDescription: EditText?= null
    private var maxNbOfAttenders: EditText?= null
    private var categorySpinner: Spinner?= null
    private var createEvent: Button?= null
    private var date : TextView? = null
    private var dateButton: Button?= null
    private var categorySelected: String?= null
    private var categories : Array<String>?=null
    private var cal : Calendar?= null
    private var dateSetListener: DatePickerDialog.OnDateSetListener?=null
    private var email: String?=null
    private var userId: String?= null

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        date!!.text = sdf.format(cal?.time)
    }

    private fun prepareCalender(){
        this.dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal?.set(Calendar.YEAR, year)
                cal?.set(Calendar.MONTH, monthOfYear)
                cal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
    }

    private fun getTodayDateByDefault(){
        DatePickerDialog(context!!,R.style.DialogTheme,
            this.dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            this.cal!!.get(Calendar.YEAR),
            this.cal!!.get(Calendar.MONTH),
            this.cal!!.get(Calendar.DAY_OF_MONTH))
        updateDateInView()
    }

    private fun chooseDate(){
        DatePickerDialog(context!!,R.style.DialogTheme,
            this.dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            this.cal!!.get(Calendar.YEAR),
            this.cal!!.get(Calendar.MONTH),
            this.cal!!.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.eventName = view?.findViewById<View>(R.id.eventNameText) as EditText
        this.eventLocation = view?.findViewById<View>(R.id.eventLocationText) as EditText
        this.eventDescription = view?.findViewById<View>(R.id.eventDescriptionText) as EditText
        this.maxNbOfAttenders = view?.findViewById<View>(R.id.maxNoAttendersText) as EditText
        this.date = view?.findViewById<View>(R.id.selectDate) as TextView
        this.dateButton = view?.findViewById<View>(R.id.changeDate) as Button
        this.categorySpinner = view?.findViewById<View>(R.id.CategorySpinner) as Spinner
        this.createEvent = view?.findViewById<View>(R.id.CreateEventButton) as Button
        this.categories = arrayOf("Football", "BasketBall", "Boxing", "Sport", "Dancing", "Indian Dancing", "Running")
        this.cal = Calendar.getInstance()!!
        this.email = if(this.arguments!!.getString("Email Address").isNullOrBlank().not()) this.arguments!!.getString("Email Address") else "ihussein@gmail.com"
        this.userId = if (this.arguments!!.getString("user Id").isNullOrBlank().not()) this.arguments!!.getString("user Id") else "31"
        //Adapter for spinner
        this.categorySpinner?.adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            this.categories!!
        )
        this.categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
                categorySelected = categories!![0]
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categorySelected = categories!![p2]
            }

        }//end of spinner
        // handling calender
        prepareCalender() // prepare calender to use it
        getTodayDateByDefault() // parse today's date in view
        this.dateButton!!.setOnClickListener { chooseDate() } // choose date when clicking on button
        // end of handling calender

        // handling network and data regex
        this.createEvent!!.setOnClickListener {

           if(this.eventName!!.text.isNotEmpty() && this.eventName!!.text.isNotBlank() && !this.eventName!!.text.isNullOrBlank() && !this.eventName!!.text.isNullOrEmpty()){
               eventNameLayout.isErrorEnabled = false
               if(this.eventLocation!!.text.isNotEmpty() && this.eventLocation!!.text.isNotBlank() && !this.eventLocation!!.text.isNullOrEmpty() && !this.eventLocation!!.text.isNullOrBlank()){
                   eventLocationLayout.isErrorEnabled = false
                   if(this.eventDescription!!.text.isNotEmpty() && this.eventDescription!!.text.isNotBlank() && !this.eventDescription!!.text.isNullOrEmpty() && !this.eventDescription!!.text.isNullOrBlank()){
                        eventDescriptionLayout.isErrorEnabled = false
                       if(this.maxNbOfAttenders!!.text.isNotEmpty() && this.maxNbOfAttenders!!.text.isNotBlank() && !this.maxNbOfAttenders!!.text.isNullOrEmpty() && !this.maxNbOfAttenders!!.text.isNullOrBlank()){
                        maxNoAttendersLayout.isErrorEnabled = false
                                // request/response to/from server
                               Fuel.get("http://10.0.2.2/Android/TalentsBoard/createEventProcess.php?" +
                                       "EventName=${this.eventName?.text?.trim().toString()}&" +
                                       "EventLocation=${this.eventLocation?.text?.trim().toString()}&" +
                                       "EventDescription=${this.eventDescription?.text?.trim().toString()}&" +
                                       "Category=${this.categorySelected?.trim().toString()}&" +
                                       "Email=${this.email!!.trim()}&" +
                                       "Date=${this.date!!.text!!.toString().replace("/","-").trim()}&" +
                                       "MaxAttenders=${this.maxNbOfAttenders?.text?.trim().toString()}")
                                   .responseString { _, _, result ->
                                       if(result.get().toLowerCase()=="done"){

                                           val bundle = Bundle()
                                           bundle.putString("Event Created","Event ${this.eventName!!.text.trim()} Created Successfully")
                                           val fragment = EventsFragment()
                                           val transaction = activity?.supportFragmentManager?.beginTransaction()
                                           fragment.arguments = bundle
                                           transaction?.replace(R.id.linear_main, fragment)
                                           transaction?.commit()
                                       } else {
                                            Toast.makeText(context,result.toString(),Toast.LENGTH_LONG).show()
                                       }

                                   } // end of network

                        } else {
                            this.maxNoAttendersLayout.error = "Max number of attenders is required"
                           this.maxNbOfAttenders!!.requestFocus()
                        }
                   } else {
                       this.eventDescriptionLayout.error = "Event description is required"
                       this.eventDescription!!.requestFocus()
                   }
               } else {
                   this.eventLocationLayout.error = "Event location is required"
                   this.eventLocation!!.requestFocus()
               }
           } else {
               this.eventNameLayout.error = "Event name is required"
               this.eventName!!.requestFocus()
           }

        } // end of networking and data regex

    } // end of main

} // end of class
