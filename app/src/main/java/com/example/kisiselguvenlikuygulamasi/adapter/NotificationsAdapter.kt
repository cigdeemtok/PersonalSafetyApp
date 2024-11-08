package com.example.kisiselguvenlikuygulamasi.adapter

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kisiselguvenlikuygulamasi.databinding.FriendRequestItemBinding
import com.example.kisiselguvenlikuygulamasi.model.AddedFriend

class NotificationsAdapter (private var personList : ArrayList<AddedFriend>,
                            private val acceptClickListener : (AddedFriend) -> Unit,
                            private val declineClickListener : (AddedFriend) -> Unit,
                            )
    : RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsHolder {
        val binding = FriendRequestItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationsHolder(binding)
    }

    override fun getItemCount(): Int = personList.size

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        val person = personList[position]

        holder.binding.requestKullaniciAdi.text = person.adSoyad



        holder.binding.butonEkle.setOnClickListener {
            acceptClickListener(person)
        }
        holder.binding.butonReddet.setOnClickListener {
            declineClickListener(person)

        }



    }

    class NotificationsHolder(val binding : FriendRequestItemBinding) : RecyclerView.ViewHolder(binding.root){

    }
}
