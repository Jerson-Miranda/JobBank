package com.example.jobbank.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobbank.databinding.FragmentJobAddDataBinding
import com.example.jobbank.model.adapter.DataAdapter
import com.example.jobbank.model.adapter.JobTouchListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface OnButtonClickListener {
    fun onButtonClicked(parameter: String?, view: String)
}

class JobAddData : Fragment() {
    private var mListener: OnButtonClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as OnButtonClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnButtonClickListener")
        }
    }

    private lateinit var binding: FragmentJobAddDataBinding
    private val database = Firebase.database
    private val resultList = mutableListOf<String>()
    private lateinit var btn: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobAddDataBinding.inflate(inflater, container, false)
        val view = binding.root

        btn = arguments?.getString("title").toString()
        binding.tvTitleJobAddData.text = btn
        visualize()
        eventsClick()
        return view
    }

    private fun visualize(){
        val ref = database.getReference("dataapp").child(arguments?.getString("query").toString()).orderByValue()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resultList.clear()
                for (snap in snapshot.children) {
                    val item = snap.getValue(String::class.java)
                    resultList.add(item!!)
                }
                binding.rvJobAddData.layoutManager = LinearLayoutManager(activity)
                binding.rvJobAddData.adapter = DataAdapter(resultList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun eventsClick(){
        binding.rvJobAddData.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvJobAddData, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val parameter = resultList[position]
                    mListener?.onButtonClicked(parameter, btn)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )
    }
}