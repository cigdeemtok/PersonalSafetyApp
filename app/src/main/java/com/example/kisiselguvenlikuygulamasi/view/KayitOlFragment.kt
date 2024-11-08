package com.example.kisiselguvenlikuygulamasi.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentKayitOlBinding
import com.example.kisiselguvenlikuygulamasi.service.LocationService


class KayitOlFragment : Fragment() {

    private lateinit var binding: FragmentKayitOlBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKayitOlBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //navigate to sign in screen
        binding.alertTextClickable.setOnClickListener {
            val direction = KayitOlFragmentDirections.actionKayitOlFragmentToGirisYapFragment()
            findNavController().navigate(direction)
        }

        //get information of user
        binding.signUpButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                val direction = KayitOlFragmentDirections.actionKayitOlFragmentToKayitInfoFragment(
                    email,
                    password
                )
                findNavController().navigate(direction)


            }else{
                Toast.makeText(requireContext(),"Lütfen geçerli bilgiler giriniz!",Toast.LENGTH_SHORT).show()
            }
        }


        super.onViewCreated(view, savedInstanceState)
    }

}