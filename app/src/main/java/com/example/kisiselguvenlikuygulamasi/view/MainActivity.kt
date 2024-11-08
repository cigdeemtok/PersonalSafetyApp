package com.example.kisiselguvenlikuygulamasi.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.databinding.ActivityMainBinding
import com.example.kisiselguvenlikuygulamasi.service.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //necessary location permissions for location tracking
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION)
                , REQUEST_LOCATION_PERMISSION)
        }else{
            startLocationService()
        }

        checkLocationService()

        if(auth.currentUser != null){
            val intent = Intent(this,LocationService::class.java)
            startLocationService()
        }

        //defining navigation bar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavView = binding.bottomNavBar

        //make navigation bar go ne for unnecessary screens like sign in, register etc.
        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.girisYapFragment){
                bottomNavView.visibility =  View.GONE
            }
            else if(destination.id == R.id.kayitInfoFragment){
                bottomNavView.visibility =  View.GONE
            }
            else if(destination.id == R.id.kayitOlFragment){
                bottomNavView.visibility =  View.GONE
            }
            else if(destination.id == R.id.mapsFragment){
                bottomNavView.visibility =  View.GONE
            }
            else if(destination.id == R.id.seeFriendProfileFragment){
                bottomNavView.visibility =  View.GONE
            }
            else{
                bottomNavView.visibility = View.VISIBLE
            }

        }
        setupWithNavController(bottomNavView,navController)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                startLocationService()
            }else{
                showPermissionDialog()
            }
        }
    }

    //permisson dialog for location tracking
    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konum İzni Gerekli!")
            .setMessage("Uygulamanın düzgün çalışması için konum izni gerekli. Ayarlara giderek izin verebilirsiniz.")
            .setPositiveButton("Ayarlar"){ _ , _ ->
                openSettings()
            }
            .setNegativeButton("Kapat"){ dialog, _ ->
                dialog.dismiss()
                showPermissionRationale()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionRationale() {
        Snackbar.make(findViewById(android.R.id.content),"Uygulamanın düzgün çalışması için konum izni gerekli.",Snackbar.LENGTH_INDEFINITE)
            .setAction("İzin Ver"){
                ActivityCompat.requestPermissions(
                    this
                    , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            }.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package",packageName,null)
        intent.data = uri
        startActivity(intent)
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this , LocationService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(serviceIntent)

        }else{
            startService(serviceIntent)
        }
    }

    //check location service is open or not
    private fun checkLocationService(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(!isLocationOpen){
            AlertDialog.Builder(this)
                .setTitle("Konumu aç")
                .setMessage("Konum izinleri gerekli. Lütfen konumu açın.")
                .setPositiveButton("Konum Ayarları"){ _ , _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }.setNegativeButton("İptal",null)
                .show()
        }
    }


    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
