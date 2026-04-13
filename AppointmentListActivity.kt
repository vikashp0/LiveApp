package com.example.liveapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveapp.adapter.AppointmentAdapter
import com.example.liveapp.databinding.ActivityAppointmentListBinding
import com.example.liveapp.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val appointmentList = mutableListOf<Appointment>()
    private lateinit var adapter: AppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        fetchAppointments()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(appointmentList)
        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    private fun fetchAppointments() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("appointments")
            .whereEqualTo("patientId", userId)
            .get()
            .addOnSuccessListener { result ->
                appointmentList.clear()
                for (document in result) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointmentList.add(appointment)
                }
                adapter.notifyDataSetChanged()
            }
    }
}