package com.example.kisiselguvenlikuygulamasi.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kisiselguvenlikuygulamasi.adapter.NotificationsAdapter
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentNotificationsBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class NotificationsFragment : Fragment() {

    private lateinit var binding : FragmentNotificationsBinding
    private lateinit var reference  : DatabaseReference
    private lateinit var friendRef  : DatabaseReference
    private lateinit var reqRef  : DatabaseReference

    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : NotificationsAdapter
    private lateinit var personList : ArrayList<AddedFriend>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //this is notifications screen to show user all friend requests

        auth = Firebase.auth

        reference = Firebase.database.reference
        reqRef = Firebase.database.getReference("Requests")
        personList = arrayListOf()

        friendRef = Firebase.database.getReference("Friends")

        binding.notificationsAlertText.visibility = View.VISIBLE
        binding.recyclerNotifications.visibility = View.GONE
        findSendedRequests()

        super.onViewCreated(view, savedInstanceState)
    }
    fun findSendedRequests() {
        reqRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                personList.clear()
                for ( data in snapshot.children){
                    val receiverID = data.key ?: continue

                    if(data.child("status").getValue(String::class.java) == "received"){
                        reference.child("UserInfo").child(receiverID).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if(snapshot.exists()){
                                    val adSoyad = snapshot.child("adSoyad").getValue(String::class.java)
                                    val telNo = snapshot.child("telefonNo").getValue(String::class.java)

                                    val reqSend = AddedFriend(receiverID,adSoyad,telNo)

                                    personList.add(reqSend)

                                    //make it show on adapter
                                    if(personList.isNotEmpty()){
                                        binding.notificationsAlertText.visibility = View.GONE
                                        binding.recyclerNotifications.visibility = View.VISIBLE
                                        binding.recyclerNotifications.layoutManager = LinearLayoutManager(activity,
                                            LinearLayoutManager.VERTICAL,false)
                                        adapter = NotificationsAdapter(personList,
                                            acceptClickListener = {
                                                acceptFriendRequest(it.id.toString())
                                                adapter.notifyDataSetChanged()
                                            },
                                            declineClickListener = {
                                                Log.d("id list",it.id.toString())
                                                print(it.id.toString())
                                                cancelFriendRequest(it.id.toString())
                                                adapter.notifyDataSetChanged()
                                            })


                                        binding.recyclerNotifications.adapter = adapter

                                    }
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun acceptFriendRequest(id : String) {
        reference.child("UserInfo").child(id).get().addOnSuccessListener { value->
            if(value.exists() && value != null){
                val adSoyad = value.child("adSoyad").getValue(String::class.java)
                val telNo = value.child("telefonNo").getValue(String::class.java)


                val friendMap = hashMapOf<String,String>()
                friendMap.put("adSoyad",adSoyad!!)
                friendMap.put("telefonNo",telNo!!)

                //add to current user's friend list
                friendRef.child(auth.currentUser!!.uid).child(id).setValue(friendMap)
                    .addOnSuccessListener {

                        reference.child("UserInfo").child(auth.currentUser!!.uid).get()
                            .addOnSuccessListener { result->

                                if(result.exists() && result != null){
                                    val currentAdSoyad = result.child("adSoyad").getValue(String::class.java)
                                    val currentTelNo = result.child("telefonNo").getValue(String::class.java)

                                    val userMap = hashMapOf<String,String>()
                                    userMap.put("adSoyad",currentAdSoyad!!)
                                    userMap.put("telefonNo",currentTelNo!!)

                                    friendRef.child(id).child(auth.currentUser!!.uid).setValue(userMap)
                                        .addOnSuccessListener {
                                            //friend added successfully
                                            Toast.makeText(activity,"Arkadas basarıyla eklendi.",Toast.LENGTH_SHORT).show()
                                            //delete the request
                                            cancelFriendRequest(id)


                                    }
                                        .addOnFailureListener {
                                            Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_SHORT).show()
                                        }
                            }
                        }
                            .addOnFailureListener {
                                Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        }


                }.addOnFailureListener {
                    Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }

        }.addOnFailureListener {
          Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }

    fun cancelFriendRequest(id :String){
        reqRef.child(auth.currentUser!!.uid).child(id).removeValue().addOnSuccessListener {
            Log.d("id list","id ic $id")

            reqRef.child(id).child(auth.currentUser!!.uid).removeValue().addOnSuccessListener {
                Toast.makeText(activity,"İstek başarılı bir şekilde iptal edildi!",Toast.LENGTH_SHORT).show()
                personList.removeAll { it.id == id }
                adapter.notifyDataSetChanged()
                //findSendedRequests()

            }.addOnFailureListener {
                Toast.makeText(activity,"İstek iptal edilemedi!",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(activity,"İstek iptal edilemedi!",Toast.LENGTH_SHORT).show()

        }
    }




}
