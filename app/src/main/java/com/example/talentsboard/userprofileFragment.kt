package com.example.talentsboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.fragment_complete_profile.*

@SuppressLint("ValidFragment")
class userprofileFragment constructor(user :String) : Fragment()  {


    var user:String?=null
    init {
        this.user=user
    }

    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/User.php"
    private var first_name : TextView?= null
    private var user_image : ImageView?= null
    private var followers : TextView?= null
    private var following : TextView?= null
    private var country : TextView?= null
    private var bell : ImageView?= null
    private lateinit var listView: ListView
    private var follow : Button?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_userprofile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user_id=6
        first_name=view?.findViewById<TextView>(R.id.username)
        country=view?.findViewById<TextView>(R.id.country)
        followers=view?.findViewById<TextView>(R.id.followers)
        following = view?.findViewById<TextView>(R.id.followings)
        follow = view?.findViewById<Button>(R.id.follow)
        bell=view?.findViewById<ImageView>(R.id.bell)
        user_image=view?.findViewById(R.id.user_image)
        Fuel.get(url!!, listOf("user" to user)).responseJson{ request, response, result ->
            first_name!!.text=result.get().array().getJSONObject(0).getString("first_name").toString()+" "+result.get().array().getJSONObject(0).getString("last_name").toString()
            country!!.text=result.get().array().getJSONObject(0).getString("country").toString()
//            Toast.makeText(activity,result.get().array().getJSONObject(0).names().length().toString(),Toast.LENGTH_LONG).show()
            var photo=result.get().array().getJSONObject(0).getString("profile").toString()
            var imageres =
                resources.getIdentifier("drawable/" + photo!!.split(".")[0], null, activity!!.packageName)
            user_image!!.setImageResource(imageres)

        }

        Fuel.get(url!!, listOf("following" to user)).responseJson{ request, response, result ->
            following!!.text=result.get().content

        }

        Fuel.get(url!!, listOf("followers" to user)).responseJson{ request, response, result ->
            followers!!.text=result.get().content
        }




        Fuel.get(url!!, listOf("QuestAndAns" to user)).responseJson{ request, response, result ->

            listView = view?.findViewById<ListView>(R.id.info_list)!!

            val listItems = arrayOfNulls<String>(result.get().array().getJSONObject(0).names().length())

            for (i in 0 until result.get().array().getJSONObject(0).names().length()) {

                listItems[i] = result.get().array().getJSONObject(0).names()[i].toString()+" : "+ result.get().array().getJSONObject(0).getString(result.get().array().getJSONObject(0).names()[i].toString())
            }
                val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter as ListAdapter?

        }


        var followed:Int=-1
        var ring:Int=-1

        Fuel.get(url!!, listOf("check" to user,"user_id" to user_id)).responseJson{ request, response, result ->

            if (result.get().array()[0].toString() == "1"){
                followed=1
                Fuel.get(url!!, listOf("select" to user,"user_id" to user_id)).responseJson{ request, response, result ->

                    if(result.get().array()[0].toString()=="1") {
                        bell!!.setImageResource(R.drawable.bell_ring)
                        ring=1
                    }
                    else
                    {
                        ring=0
                    }
                }


                follow!!.text="Following"
                bell!!.visibility=View.VISIBLE
            }
            else if (result.get().array()[0].toString() == "0")
            {
                follow!!.text="Follow"
                followed=0
                bell!!.visibility=View.INVISIBLE
            }

            }



        follow!!.setOnClickListener {
            if (followed==1){
                follow!!.text="Follow"
                Fuel.get(url!!, listOf("unfollow" to user,"user_id" to user_id)).responseJson{ request, response, result ->

                }
                bell!!.visibility=View.INVISIBLE
                followed=0

            }
            else if (followed==0)
            {
                follow!!.text="Following"
                bell!!.setImageResource(R.drawable.bell)
                Fuel.get(url!!, listOf("follow" to user,"user_id" to user_id)).responseJson{ request, response, result ->
                }
                bell!!.visibility=View.VISIBLE

                followed=1
            }

        }

            bell!!.setOnClickListener {

                if(ring==0){

                    bell!!.setImageResource(R.drawable.bell_ring)
                    Fuel.get(url!!, listOf("Ring" to user,"user_id" to user_id)).responseJson{ request, response, result ->

                    }
                    ring=1
                }

                else if(ring==1){
                    bell!!.setImageResource(R.drawable.bell)
                    Fuel.get(url!!, listOf("un_ring" to user,"user_id" to user_id)).responseJson{ request, response, result ->

                    }
                    ring=0
                }
            }


    }



}
