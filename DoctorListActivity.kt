package com.example.liveapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveapp.adapter.DoctorAdapter
import com.example.liveapp.databinding.ActivityDoctorListBinding
import com.example.liveapp.model.Doctor
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class DoctorListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorListBinding
    private lateinit var db: FirebaseFirestore
    private val allDoctors = mutableListOf<Doctor>()
    private val filteredDoctors = mutableListOf<Doctor>()
    private lateinit var adapter: DoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        
        setupRecyclerView()
        fetchDoctors()
        setupSearch()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = DoctorAdapter(filteredDoctors)
        binding.rvDoctors.layoutManager = LinearLayoutManager(this)
        binding.rvDoctors.adapter = adapter
    }

    private fun fetchDoctors() {
        db.collection("doctors").get()
            .addOnSuccessListener { result ->
                allDoctors.clear()
                for (document in result) {
                    val doctor = document.toObject(Doctor::class.java)
                    allDoctors.add(doctor.copy(id = document.id))
                }
                filterDoctors("")
            }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterDoctors(query: String) {
        filteredDoctors.clear()
        if (query.isEmpty()) {
            filteredDoctors.addAll(allDoctors)
        } else {
            val lowerQuery = query.lowercase(Locale.getDefault())
            for (doctor in allDoctors) {
                if (doctor.name.lowercase(Locale.getDefault()).contains(lowerQuery) ||
                    doctor.specialty.lowercase(Locale.getDefault()).contains(lowerQuery)) {
                    filteredDoctors.add(doctor)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}