package com.example.liveapp

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveapp.databinding.ActivityBookingBinding
import com.example.liveapp.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""
        val doctorName = intent.getStringExtra("DOCTOR_NAME") ?: ""
        val specialty = intent.getStringExtra("SPECIALTY") ?: ""

        binding.tvDoctorName.text = doctorName
        binding.tvSpecialty.text = specialty

        // Set default date
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        binding.btnConfirmBooking.setOnClickListener {
            val checkedId = binding.rgTimeSlots.checkedRadioButtonId
            if (checkedId != -1) {
                val radioButton = findViewById<RadioButton>(checkedId)
                val time = radioButton.text.toString()
                
                bookAppointment(doctorId, doctorName, selectedDate, time)
            } else {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bookAppointment(doctorId: String, doctorName: String, date: String, time: String) {
        val userId = auth.currentUser?.uid ?: return
        val appointmentId = db.collection("appointments").document().id
        
        val appointment = Appointment(
            id = appointmentId,
            patientId = userId,
            doctorId = doctorId,
            doctorName = doctorName,
            date = date,
            time = time,
            status = "Scheduled"
        )

        db.collection("appointments").document(appointmentId).set(appointment)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment Booked Successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}