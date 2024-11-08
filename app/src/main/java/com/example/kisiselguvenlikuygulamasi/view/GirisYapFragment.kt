package com.example.kisiselguvenlikuygulamasi.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentGirisYapBinding
import com.example.kisiselguvenlikuygulamasi.service.LocationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class GirisYapFragment : Fragment() {

    private lateinit var binding: FragmentGirisYapBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGirisYapBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth




        //remembers the user if already logged in
        val currentUser = auth.currentUser

        if(currentUser != null){
            val direction = GirisYapFragmentDirections.actionGirisYapFragmentToHomeFragment()
            findNavController().navigate(direction)
        }

        //if user is not registered, send them to registration page
        binding.alertTextClickable.setOnClickListener {
            val direction = GirisYapFragmentDirections.actionGirisYapFragmentToKayitOlFragment()
            findNavController().navigate(direction)
        }

        //control user's information and log them in
        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            //control inputs are null or not
            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener {
                        //sign in is successful

                        //Location of current user is starting to saved to database by location service
                        //Start the service again with the new user's context
                        activity?.startService(Intent(activity, LocationService::class.java))

                        Toast.makeText(requireContext(),"Giris Bsaarili!",Toast.LENGTH_SHORT).show()
                        //navigate user that is logged in to home page
                        val direction =
                            GirisYapFragmentDirections.actionGirisYapFragmentToHomeFragment()
                        findNavController().navigate(direction)
                    }
                    .addOnFailureListener {
                        //sign in is unsuccessful - show an error message
                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
            }else{
                //inputs are incorrect
                Toast.makeText(requireContext(),"Lütfen geçerli bilgiler giriniz!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}