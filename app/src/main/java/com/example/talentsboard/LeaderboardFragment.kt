package com.example.talentsboard

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.clans.fab.FloatingActionButton
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import java.lang.Exception


class LeaderboardFragment : Fragment() {

    private var url: String?= null
    private var categorySpinner: Spinner?= null
    private var categorySelected: String?= null
    private var categories: Array<String>?= null
    private var list: ListView?= null

    fun network(url: String){

        Fuel.get(url).responseJson { _, _, result ->

            val size: Int = result.get().array().length()
                val userId = arrayOfNulls<String?>(size)
                val fullName = arrayOfNulls<String?>(size)
                val rating = arrayOfNulls<String?>(size)

                try {
                    for (i in 0 until size){

                        userId[i] = result.get().array().getJSONObject(i).getString("userId")
                        fullName[i] = result.get().array().getJSONObject(i).getString("firstName")+" "+result.get().array().getJSONObject(i).getString("lastName")
                        rating[i] = result.get().array().getJSONObject(i).getString("rating")

                    }
                } catch (e: Exception){
                    Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
                }

            if(size == 0){
                    delay(noDataFound,1000,View.VISIBLE,"NO USERS FOUND") // show successful row after 1 second
                    delay(noDataFound,5000,View.GONE,"") // hide successful row after 5 seconds
            }
            this.list?.adapter = leaderboardRowAdapter(
                context!!,
                size,
                userId,
                fullName,
                rating
            )

        } // end of network

    }

    private fun delay(v: View, t: Long,action: Int,msg: String){
        noDataFound.postDelayed( {
            v.visibility = action
            leaderboardnouserfound.text = msg
        },t)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.url = "http://10.0.2.2/Android/TalentsBoard/leaderboard.php?Category="
//        this.fab = view?.findViewById(R.id.fab) as FloatingActionButton
        this.list = view?.findViewById(R.id.list) as ListView
        this.categorySpinner = view?.findViewById(R.id.spinner)as Spinner
        this.categories = arrayOf("All","Football", "BasketBall", "Boxing", "Sports", "Dancing", "Acting","Comedy","Singing")
        this.categorySelected = categories!![0]

        this.categorySpinner?.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1,
            this.categories
        )

        this.categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
                network(url!!)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categorySelected = categories!![p2]
                network("http://10.0.2.2/Android/TalentsBoard/leaderboard.php?Category=${categorySelected.toString()}")
            }

        }//end of spinner

    } // end of main
    //    @SuppressLint("ValidFragment")
    private class leaderboardRowAdapter(
        context: Context,
        size: Int,
        userId: Array<String?>,
        fullName: Array<String?>,
        rating: Array<String?>
    ) : BaseAdapter(){

        private val layoutInflater: LayoutInflater // inflater in order to inflate each row and fetch user info
        // data from construtor must be initialize in init
        private val mContext: Context
        private val size: Int
        private val names: Array<String?>
        private val rating: Array<String?>
        private val userId: Array<String?>
        // data to inflate each row
        private var row: View?= null
        // data shown in list view
        private var name: TextView?= null
        private var photo: ImageView?= null
        private var rank: TextView?= null
        private var rate: TextView?= null

        init {

            this.mContext = context
            this.layoutInflater=LayoutInflater.from(mContext)
            this.size = size
            this.userId = userId
            this.names = fullName
            this.rating = rating

        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return this.size
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

            row = layoutInflater.inflate(R.layout.leaderboard_row, viewGroup,false)

            this.rank = row!!.findViewById<View>(R.id.rankTextView) as TextView
            this.photo = row!!.findViewById<View>(R.id.userImageView) as ImageView
            this.name = row!!.findViewById<View>(R.id.fullNameTextView) as TextView
            this.rate= row!!.findViewById<View>(R.id.ratingTextView) as TextView

            this.rank!!.text = "#${position+1}"
            this.name!!.text = this.names[position]
            this.rate!!.text = this.rating[position]

            return row!!

        }

    }

}