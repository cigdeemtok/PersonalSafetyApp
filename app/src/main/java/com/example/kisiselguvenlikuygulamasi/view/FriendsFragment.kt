package com.example.kisiselguvenlikuygulamasi.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kisiselguvenlikuygulamasi.adapter.AddedFriendAdapter
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentFriendsBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : AddedFriendAdapter
    private lateinit var personList : ArrayList<AddedFriend>
    private lateinit var friendsList : ArrayList<AddedFriend>
    private lateinit var reference  : DatabaseReference
    private lateinit var reqRef  : DatabaseReference
    private lateinit var friendsRef  : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = Firebase.auth

        reference = Firebase.database.reference
        reqRef = Firebase.database.getReference("Requests")
        friendsRef = Firebase.database.getReference("Friends")

        personList = arrayListOf()
        friendsList = arrayListOf()


        //if there is already added friends find and show them
        findAddedFriends()

        //adding friends with their phone number
        binding.buttonAddFriend.setOnClickListener {
            val searchNo = binding.edtTextAddFriend.text.toString()
            //check if already friends
            if(!controlRequests(searchNo)){
                //send request
                sendFriendRequest(searchNo)
            }
        }
            super.onViewCreated(view, savedInstanceState)

    }

    fun findAddedFriends(){
        friendsRef.child(auth.currentUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val friendID = data.key
                        val adSoyad = data.child("adSoyad").getValue(String::class.java)
                        val telNo = data.child("telefonNo").getValue(String::class.java)

                        val reqSend = AddedFriend(friendID,adSoyad,telNo)

                        friendsList.add(reqSend)

                        //make it show on adapter
                        if(friendsList.isNotEmpty()){
                            binding.recyclerFriends.visibility = View.VISIBLE
                            binding.noFriendsAlertText.visibility = View.GONE
                            binding.recyclerFriends.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
                            adapter = AddedFriendAdapter(friendsList
                                , izleClickListener = {
                                val direction = FriendsFragmentDirections.actionFriendsFragmentToMapsFragment(it.id!!,it.adSoyad!!)
                                findNavController().navigate(direction)
                            }, profilClickListener = {
                                if(it.id != null && it.adSoyad != null){
                                    val direction = FriendsFragmentDirections.actionFriendsFragmentToSeeFriendProfileFragment(it.id!!,it.adSoyad!!)
                                    findNavController().navigate(direction)
                                }
                                 //Log.d("id list",it.id.toString())
                                //print(it.id.toString())
                            }, deleteClickListener = {
                                deleteFriends(it.id.toString())
                            })

                            binding.recyclerFriends.adapter = adapter

                        }

                        else{
                            binding.recyclerFriends.visibility = View.GONE
                            binding.noFriendsAlertText.visibility = View.VISIBLE
                        }
                        adapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    //before sending request control if user is already a friend or not
    fun controlRequests( reqNo : String) : Boolean {
        var checkReq = false
        reference.child("UserInfo").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for( result in snapshot.children){
                        val telNo = result.child("telefonNo").getValue(String::class.java)
                        val userId = result.key
                        //if request exist already
                        if(telNo == reqNo && auth.currentUser!!.uid != userId){
                            Toast.makeText(activity,"İstek mevcut",Toast.LENGTH_SHORT).show()
                            checkReq = true
                            break
                        }
                        else if(auth.currentUser!!.uid == userId){
                            Toast.makeText(activity,"Kendinize istek atamazsınız!",Toast.LENGTH_SHORT).show()
                            checkReq = true
                            break
                        }
                        else{
                            checkReq = false
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,error.message,Toast.LENGTH_SHORT).show()
            }

        })
        return checkReq
    }


    // delete friends function
    private fun deleteFriends(id: String) {
        friendsRef.child(auth.currentUser!!.uid).child(id).removeValue().addOnSuccessListener {

            friendsRef.child(id).child(auth.currentUser!!.uid).removeValue().addOnSuccessListener {
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


    fun sendFriendRequest(searchNo : String){
        var userFound = false
        reference.child("UserInfo").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children) {
                    val dbTelNo = data.child("telefonNo").getValue(String::class.java)
                    val reqId = data.key


                    if (dbTelNo != null && dbTelNo == searchNo && auth.currentUser!!.uid != reqId ) {
                        userFound = true
                        val userId = data.key

                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Arkadaşlık isteği")
                        builder.setMessage("Bu kişiye arkadaşlık isteği gönderilsin mi?")
                        builder.setPositiveButton("Evet") { dialog, which ->
                            //send friend request
                            reqRef.child(auth.currentUser!!.uid).child(userId!!).child("status")
                                .setValue("sent")
                                .addOnSuccessListener {
                                    reqRef.child(userId).child(auth.currentUser!!.uid).child("status")
                                        .setValue("received")
                                        .addOnSuccessListener {
                                            //clear edit text
                                            binding.edtTextAddFriend.text.clear()

                                            Toast.makeText(
                                                activity,
                                                "İstek basariyla gönderildi!",
                                                Toast.LENGTH_LONG).show()


                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                it.localizedMessage,
                                                Toast.LENGTH_SHORT).show()
                                        }
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        activity,
                                        it.localizedMessage,
                                        Toast.LENGTH_SHORT).show()
                                }


                        }
                        builder.setNegativeButton("Hayır") { dialog, which ->
                            //cancel sending request
                            Toast.makeText(
                                activity,
                                "Arkadaşlık isteği gönderilmedi.",
                                Toast.LENGTH_SHORT).show()
                        }

                        val dialog = builder.create()
                        dialog.show()
                        break

                    }
                    if(!userFound) {
                        Toast.makeText(
                            activity,
                            "Aradığınız kullanıcı bulunamamaktadır!",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}