package com.example.kisiselguvenlikuygulamasi.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentKayitInfoBinding
import com.example.kisiselguvenlikuygulamasi.service.LocationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class KayitInfoFragment : Fragment() {

    private lateinit var binding: FragmentKayitInfoBinding
    val args : KayitInfoFragmentArgs by navArgs()
    private lateinit var auth : FirebaseAuth
    private lateinit var reference : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentKayitInfoBinding.inflate(inflater,container,false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        auth = Firebase.auth
        reference = Firebase.database.reference


        val email = args.email
        val password = args.password

        //println("Email: $email")
        //println("Email: $password")
        //Log.d("args",email)
        //Log.d("args",password)

        //get user information of user that trying to register
        binding.girisInfoButon.setOnClickListener {
            val adres = binding.editAdres.text.toString()
            val kanGrubu = binding.editKan.text.toString()
            val alerjiler = binding.editAlerji.text.toString()
            val ilaclar = binding.editIlac.text.toString()
            val tibbiNotlar = binding.editHastalik.text.toString()
            val organBagisi = binding.editOrgan.text.toString()
            val telefonNo = binding.editPhone.text.toString()
            val adSoyad = binding.editAdSoyad.text.toString()



            if (email.isNotEmpty() && password.isNotEmpty()){
                //registration of user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnSuccessListener {
                    //if registration is successful, save information to database
                        val email = auth.currentUser!!.email
                        val infoMap = hashMapOf<String,String>()
                        infoMap.put("adres",adres)
                        infoMap.put("kanGrubu",kanGrubu)
                        infoMap.put("alerjiler",alerjiler)
                        infoMap.put("ilaclar",ilaclar)
                        infoMap.put("tibbiNotlar",tibbiNotlar)
                        infoMap.put("organBagisi",organBagisi)
                        infoMap.put("telefonNo",telefonNo)
                        infoMap.put("adSoyad",adSoyad)
                        infoMap.put("email",email!!)


                        //saving to database
                        reference.child("UserInfo").child(auth.currentUser!!.uid).setValue(infoMap).addOnSuccessListener {
                            //start location service for current user
                            // Start the service again with the new user's context
                            activity?.startService(Intent(activity, LocationService::class.java))
                            Toast.makeText(requireContext(),"Kayıt başarıyla tamamlandı!",Toast.LENGTH_SHORT).show()
                            val direction =
                                KayitInfoFragmentDirections.actionKayitInfoFragmentToHomeFragment()
                            findNavController().navigate(direction)
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

                        }

                }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

}