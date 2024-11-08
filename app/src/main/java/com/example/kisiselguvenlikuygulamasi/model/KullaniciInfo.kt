package com.example.kisiselguvenlikuygulamasi.model

data class KullaniciInfo(
    //address
    val adres : String,
    //blood type
    val kanGrubu : String,
    //allergies
    val alerjiler : String,
    //medications
    val ilaclar : String,
    //medical history
    val tibbiNotlar : String,
    //if person is up for organ donation
    val organBagisi : String,
    //phone number
    val telefonNo : String,
    //name
    val adSoyad : String
)
