package com.example.kisiselguvenlikuygulamasi

import com.example.kisiselguvenlikuygulamasi.model.UserLocation

interface UserDataCallback {
    fun getUserData(location : UserLocation, email : String)
}