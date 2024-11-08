package com.example.kisiselguvenlikuygulamasi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.databinding.AddedFriendsItemBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend
import com.example.kisiselguvenlikuygulamasi.view.FriendsFragmentDirections


class AddedFriendAdapter (private var personList : ArrayList<AddedFriend>,
                          private val izleClickListener : (AddedFriend) -> Unit,
                          private val profilClickListener : (AddedFriend) -> Unit,
                          private val deleteClickListener : (AddedFriend) -> Unit)
    : RecyclerView.Adapter<AddedFriendAdapter.AddedFriendHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedFriendHolder {
        val binding = AddedFriendsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddedFriendHolder(binding)
    }

    override fun getItemCount(): Int = personList.size

    override fun onBindViewHolder(holder: AddedFriendHolder, position: Int) {
        val person = personList[position]

        holder.binding.addedFriendName.text = person.adSoyad
        holder.binding.addedFriendTelNo.text = person.telNo


        holder.binding.butonIzle.setOnClickListener {
            izleClickListener(person)
        }
        holder.binding.butonProfileGit.setOnClickListener {
            profilClickListener(person)

        }
        holder.binding.butonSil.setOnClickListener {
            deleteClickListener(person)

        }

    }

    class AddedFriendHolder(val binding : AddedFriendsItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}