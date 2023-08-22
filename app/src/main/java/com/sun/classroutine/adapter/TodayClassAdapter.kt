package com.sun.classroutine.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.classroutine.data.ClassInfo
import com.sun.classroutine.databinding.ItemTodayClassBinding

class TodayClassAdapter(private var classList: List<ClassInfo>) :
    RecyclerView.Adapter<TodayClassAdapter.ClassViewHolder>() {

    class ClassViewHolder(private val binding: ItemTodayClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(classInfo: ClassInfo) {
            binding.classNameTextView.text = classInfo.subject
            binding.teacherNameTextView.text = classInfo.teacher
            binding.roomNumberTextView.text = classInfo.dayOfWeek
            binding.classTimeTextView.text = classInfo.startTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding =
            ItemTodayClassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentItem = classList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = classList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newClassList: List<ClassInfo>) {
        classList = newClassList
        notifyDataSetChanged()
    }
}
