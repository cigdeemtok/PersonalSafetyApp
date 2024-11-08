package com.example.kisiselguvenlikuygulamasi.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentSeeFriendProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SeeFriendProfileFragment : Fragment() {

    private lateinit var binding : FragmentSeeFriendProfileBinding
    val args : SeeFriendProfileFragmentArgs by navArgs()
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSeeFriendProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        reference = Firebase.database.getReference("UserInfo")

        //getting id info of clicked user in Friends fragment and use it here for getting friend's info
        val friendID = args.friendId
        val adSoyad = args.adSoyad

        getFriendsProfile(friendID,adSoyad)

        super.onViewCreated(view, savedInstanceState)
    }

    //in Friends fragment you can click see profile button and using this fragment show that user's information from database by their id
    private fun getFriendsProfile(id : String, adSoyad : String) {
        reference.child(id).get().addOnSuccessListener { result->
            if(result.exists() && result != null){

                binding.profileKullaniciAdi.text = adSoyad
                binding.profileAdres.text = result.child("adres").getValue(String::class.java)
                binding.profileAlerjiler.text = result.child("alerjiler").getValue(String::class.java)
                binding.profileIlaclar.text = result.child("ilaclar").getValue(String::class.java)
                binding.profileKanGrubu.text = result.child("kanGrubu").getValue(String::class.java)
                binding.profileOrganBagisi.text = result.child("organBagisi").getValue(String::class.java)
                binding.profileTelefonNo.text = result.child("telefonNo").getValue(String::class.java)
                binding.profileTibbiNotlar.text = result.child("tibbiNotlar").getValue(String::class.java)

            }
            else{
                Toast.makeText(activity,"Veri alınamıyor.",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }
}
