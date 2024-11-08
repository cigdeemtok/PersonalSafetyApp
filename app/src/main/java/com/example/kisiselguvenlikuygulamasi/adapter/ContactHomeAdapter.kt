package com.example.kisiselguvenlikuygulamasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kisiselguvenlikuygulamasi.databinding.ContactHomeItemBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend

class ContactHomeAdapter  (private var personList : ArrayList<AddedFriend>,
                           private val textClickListener : (AddedFriend) -> Unit,
                           private val callClickListener : (AddedFriend) -> Unit)
    : RecyclerView.Adapter<ContactHomeAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val binding = ContactHomeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactHolder(binding)
    }

    override fun getItemCount(): Int = personList.size

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val person = personList[position]

        holder.binding.nameText.text = person.adSoyad


        holder.binding.textButton.setOnClickListener {
            textClickListener(person)
        }
        holder.binding.callButton.setOnClickListener {
            callClickListener(person)

        }

    }

    class ContactHolder(val binding : ContactHomeItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}