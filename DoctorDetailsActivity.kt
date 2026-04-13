package com.example.liveapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.liveapp.databinding.ActivityDoctorDetailsBinding

class DoctorDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""
        val name = intent.getStringExtra("DOCTOR_NAME") ?: ""
        val specialty = intent.getStringExtra("SPECIALTY") ?: ""
        val imageUrl = intent.getStringExtra("IMAGE_URL") ?: ""
        val experience = intent.getStringExtra("EXPERIENCE") ?: ""

        binding.tvDoctorName.text = name
        binding.tvSpecialty.text = specialty
        binding.tvExperience.text = experience
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.ivDoctorLarge)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnBookNow.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra("DOCTOR_ID", doctorId)
                putExtra("DOCTOR_NAME", name)
                putExtra("SPECIALTY", specialty)
            }
            startActivity(intent)
        }
    }
}