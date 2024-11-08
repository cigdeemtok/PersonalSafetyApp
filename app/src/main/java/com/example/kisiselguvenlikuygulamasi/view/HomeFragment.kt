package com.example.kisiselguvenlikuygulamasi.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.UserDataCallback
import com.example.kisiselguvenlikuygulamasi.adapter.ContactHomeAdapter
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentHomeBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend
import com.example.kisiselguvenlikuygulamasi.model.UserLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var friendsRef : DatabaseReference
    private lateinit var friendsList : ArrayList<AddedFriend>
    private lateinit var adapter : ContactHomeAdapter
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraID : String
    private lateinit var mediaPlayer : MediaPlayer
    //private val smsPermissionCode = 101
    private lateinit var locationRef : DatabaseReference
    private var isEmailSent = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        friendsRef = Firebase.database.reference
        locationRef = Firebase.database.getReference("Locations")
        friendsList = arrayListOf()

        auth = Firebase.auth

        //make added friends show on home page
        findAddedFriends()


        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.police_operation_siren)

        binding.noFriendsHomeAlert.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToFriendsFragment()
            findNavController().navigate(direction)
        }

        val isFlashAvailable = requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)

        if(!isFlashAvailable){
            Toast.makeText(requireContext(),"Fener su anda acilamıyor.",Toast.LENGTH_SHORT).show()
        }

        cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraID = cameraManager.cameraIdList[0]
        }catch (e : CameraAccessException){
            e.printStackTrace()
        }

        //home page's lighting flash feature added to button
        binding.ligthButton.setOnCheckedChangeListener { p0, p1 -> flasiYak(p1) }

        // SOS feature added to button by sendSosMail function
        binding.sosButton.setOnClickListener {
            // couldn't make sms sos function because of operator limitations
            //sendSosMessage()
            //checkSmsPermissionAndSendSos()
            sendSosMail()
        }

        // playing the emergency siren voice feature added to button
        binding.sirenButton.setOnCheckedChangeListener { compoundButton, boolean ->
            playVoice(boolean)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    // one of main features of the app, sending sos message to emergency contact with personal information
    fun sendSosMail(){

        //check if flag is true / if mail intent has already been triggered
        if(isEmailSent){
            return //Exit the function if already sent
        }

        //set flag to true to make sure it is not re-sending
        isEmailSent = true

        getUserData(object : UserDataCallback{
            override fun getUserData(location: UserLocation, email: String) {
                val message =  "${auth.currentUser?.email} adlı kullanıcı size sos mesajı gönderdi.\n Kullanıcının konumu: https://maps.google.com/?q=${location.lat},${location.long}"

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822" //email apps only
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))//recipient mail
                    //data = Uri.parse("mailto:$email")
                    putExtra(Intent.EXTRA_SUBJECT,"SOS")
                    putExtra(Intent.EXTRA_TEXT,message)
                }
                if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
                    // Start the email activity
                    startActivity(emailIntent)
                } else {
                    // If no email client is found, show a toast or handle the case accordingly
                    Toast.makeText(requireContext(), "No email client found", Toast.LENGTH_SHORT).show()
                }


            }
        })

        resetEmailFlag()

    }
    //reset email flag with delay of 2 sec to make sure mail app opens/triggered just once
    private fun resetEmailFlag(){
        Handler(Looper.getMainLooper()).postDelayed({
            isEmailSent = false
        }, 2000)
    }

    // getting current user's latest location from database
    fun getUserData(callback: UserDataCallback){
        locationRef.child(auth.currentUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val latitude = snapshot.child("latitude").getValue(Double::class.java)
                    val longitude = snapshot.child("longitude").getValue(Double::class.java)


                    val loc = UserLocation(latitude!!, longitude!!)


                    //mail is constant to make sure it is a real mail address to test it sends sos message
                    //this is handled like this cause user infos are mockup, you can get the sender email from db
                    callback.getUserData(loc, "example@gmail.com")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //if it doesn't succeed show error message
                Toast.makeText(activity,error.message,Toast.LENGTH_SHORT).show()
            }
        } )

    }

    fun playVoice(isPlaying : Boolean){
        if(isPlaying){
            mediaPlayer.start()
        }else{
            mediaPlayer.pause()
        }
    }

    fun flasiYak(status : Boolean){
        try {
            cameraManager.setTorchMode(cameraID,status)
        }catch (e : CameraAccessException){
            e.printStackTrace()
        }
    }

    //getting all emergeny contact/added friends who has the app, and showing them in home page
    fun findAddedFriends() {
        friendsRef.child("Friends").child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val adSoyad = data.child("adSoyad").getValue(String::class.java)
                        val telNo = data.child("telefonNo").getValue(String::class.java)

                        val reqSend = AddedFriend(data.key,adSoyad,telNo)

                        friendsList.add(reqSend)

                        //make it show on adapter
                        if(friendsList.isNotEmpty()){
                            binding.contactRV.visibility = View.VISIBLE
                            binding.noFriendsHomeAlert.visibility = View.GONE
                            binding.contactRV.layoutManager = LinearLayoutManager(activity,
                                LinearLayoutManager.HORIZONTAL,false)
                            adapter = ContactHomeAdapter(friendsList, textClickListener = {
                                //text button
                                textFriend(it.telNo!!)

                            },
                            callClickListener = {
                                //call button
                                callFriend(it.telNo!!)

                            })



                            binding.contactRV.adapter = adapter

                        }
                        if(friendsList.isEmpty()){
                            binding.contactRV.visibility = View.GONE
                            binding.noFriendsHomeAlert.visibility = View.VISIBLE
                        }
                        adapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun callFriend(number : String){
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:+9$number")
        }
        if(callIntent.resolveActivity(requireContext().packageManager) != null){
            startActivity(callIntent)
        }
        else{
            Toast.makeText(activity,"Uygun uygulama bulunamadı.",Toast.LENGTH_SHORT).show()
        }
    }
    fun textFriend(number : String){
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:+9$number")
        }
        if (smsIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            Toast.makeText(requireContext(), "Uygun uygulama bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }


    // couldn't make sms sos function because of operator limitations
   /* fun checkSmsPermissionAndSendSos(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.SEND_SMS),smsPermissionCode)
        }else {
            sendSosMessage()
        }
    }
    fun sendSosMessage(){
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("SOS mesajı gönder.")
            .setMessage("Acil durum mesajı göndermek istiyor musunuz?")
            .setPositiveButton("Evet"){ dialog,_->
                dialog.dismiss()
                sendSmsWithLocation()
            }
            .setNegativeButton("Hayır"){ dialog, _ ->
                dialog.dismiss()

            }.create()

        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if(alertDialog.isShowing){
                alertDialog.dismiss()
                sendSmsWithLocation()
            }
        },5000)
    }
    fun sendSmsWithLocation(){
        getUserData(object : UserDataCallback{
            override fun getUserData(location: UserLocation, number: String) {
                val message =  "${auth.currentUser?.email} adlı kullanıcı size sos mesajı gönderdi.\n Kullanıcının konumu: https://maps.google.com/?q=${location.lat},${location.long}"

                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage("+90$number",null,message,null,null)
                Toast.makeText(activity,"Sos mesajı gönderildi.",Toast.LENGTH_SHORT).show()            }

        })


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == smsPermissionCode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
                checkSmsPermissionAndSendSos()
            }else{
                Toast.makeText(activity,"Sms izni reddedildi.",Toast.LENGTH_SHORT).show()
            }
        }
    }*/


}