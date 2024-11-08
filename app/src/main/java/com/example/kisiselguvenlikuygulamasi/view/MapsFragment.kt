package com.example.kisiselguvenlikuygulamasi.view

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.kisiselguvenlikuygulamasi.R
import com.example.kisiselguvenlikuygulamasi.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {
    private lateinit var binding : FragmentMapsBinding
    private lateinit var gMap: GoogleMap
    private lateinit var gpsLineTrack : Polyline
    private lateinit var reference: DatabaseReference
    val args : MapsFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        gMap = googleMap 

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLUE)
        polylineOptions.width(4f)
        gpsLineTrack = googleMap.addPolyline(polylineOptions)

        getUserLocations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater,container,false)
        reference = Firebase.database.getReference("Locations")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    //get current user's latest location with maps fragment
    //this function to get location(lat-long) and save it to database
    //also shows and updates the location on map fragment and marks it with user's name
    private fun getUserLocations() {
        val userId = args.friendID
        val adSoyad = args.adSoyad
        reference.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val latitude = snapshot.child("latitude").getValue(Double::class.java)
                    val longitude = snapshot.child("longitude").getValue(Double::class.java)
                    val time = snapshot.child("time").getValue(String::class.java)

                    if(latitude != null && longitude != null){
                        val userLocation = LatLng(latitude,longitude)
                        gMap.clear()
                        gMap.addMarker(
                            MarkerOptions().position(userLocation).title("$adSoyad")
                        )

                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))

                        updateTrack(userLocation)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateTrack(location : LatLng) {
        val points = gpsLineTrack.points.toMutableList()
        points.add(location)
        gpsLineTrack.points = points
        //val polylineOptions = PolylineOptions().add(location).color(Color.BLUE).width(4f)
        //gMap.addPolyline(polylineOptions)
    }
}