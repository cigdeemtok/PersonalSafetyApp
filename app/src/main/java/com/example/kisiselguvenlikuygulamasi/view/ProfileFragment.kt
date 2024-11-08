package com.example.kisiselguvenlikuygulamasi.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentProfileBinding
import com.example.kisiselguvenlikuygulamasi.service.LocationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    //add app bar
    //add edit menu part
    //add logout menu part
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth : FirebaseAuth
    //private lateinit var firestore : FirebaseFirestore
    private lateinit var reference : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = Firebase.auth

        reference = Firebase.database.getReference("UserInfo")

        //this screens shows user's information from database
        reference.child(auth.currentUser!!.uid).get().addOnSuccessListener { value->

            if(value != null && value.exists()){

                binding.profileKullaniciAdi.text = auth.currentUser!!.email
                binding.profileAdres.text = value.child("adres").getValue(String::class.java)
                binding.profileAlerjiler.text = value.child("alerjiler").getValue(String::class.java)
                binding.profileIlaclar.text = value.child("ilaclar").getValue(String::class.java)
                binding.profileKanGrubu.text = value.child("kanGrubu").getValue(String::class.java)
                binding.profileOrganBagisi.text = value.child("organBagisi").getValue(String::class.java)
                binding.profileTelefonNo.text = value.child("telefonNo").getValue(String::class.java)
                binding.profileTibbiNotlar.text = value.child("tibbiNotlar").getValue(String::class.java)

            }
            else{
                Log.d("data db", "null")
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    //inflating options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_options,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOutMenu -> {
                //signout and go to login page
                Log.d("sign out", "icerde")
                auth.signOut()

                //stop location service
                activity?.stopService(Intent(activity,LocationService::class.java))
                //go to sign in screen after signing out
                val direction = ProfileFragmentDirections.actionProfileFragmentToGirisYapFragment()
                findNavController().navigate(direction)
                //findNavController().popBackStack()


            }
            R.id.editProfileMenu -> {
                //go to edit page

            }

        }
        return super.onOptionsItemSelected(item)
    }

}