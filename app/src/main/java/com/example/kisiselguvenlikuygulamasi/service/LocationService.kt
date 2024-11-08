package com.example.kisiselguvenlikuygulamasi.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest : LocationRequest
    private lateinit var auth : FirebaseAuth
    private lateinit var reference : DatabaseReference

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth
        reference = Firebase.database.getReference("Locations")

        //location request
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        //start foreground service to get latest location
        createNotificationChannel()
        startForeground(1,createNotification())

        //start getting location updates
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            val serviceChn = NotificationChannel(CHANNEL_ID,"Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChn)
        }

    }
    private fun createNotification(): Notification? {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Konum takip ediliyor...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                for(location in p0.locations){
                    updateLocationForDatabase(location)
                }
            }
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
        }

    }

    private fun updateLocationForDatabase(location : Location) {

        //updating database frequently with current user's latest location
        val userID = auth.currentUser?.uid


        if(location != null && userID != null){

            val locationMap = hashMapOf<String,Any>()
            locationMap.put("latitude",location.latitude)
            locationMap.put("longitude",location.longitude)
            locationMap.put("time",getCurrentDate())

            reference.child(userID!!).setValue(locationMap).addOnSuccessListener {

                Toast.makeText(this,"lokasyon güncellendi.",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    companion object {
        const val CHANNEL_ID = "LocationServiceChannel"
    }
}