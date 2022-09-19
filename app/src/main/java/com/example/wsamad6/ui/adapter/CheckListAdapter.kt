package com.example.wsamad6.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad6.data.models.Symptom
import com.example.wsamad6.databinding.FragmentCheckListBinding
import com.example.wsamad6.databinding.ItemCheckListBinding

class CheckListAdapter(private val list : List<Symptom>):RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder>() {
    private val checkBoxes = mutableListOf<CheckBox>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        val binding = ItemCheckListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CheckListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class CheckListViewHolder(private val binding: ItemCheckListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:Symptom){
            binding.txt.text = item.title
            checkBoxes.add(binding.checkBox)
        }
    }
    fun buttonsChecked():List<Int>{
        val intList = mutableListOf<Int>()
        for (i in checkBoxes.indices){
            if (checkBoxes[i].isChecked) intList .add(list[i].id)
        }
        return intList
    }

}