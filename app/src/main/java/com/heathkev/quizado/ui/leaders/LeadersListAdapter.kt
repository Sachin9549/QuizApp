package com.heathkev.quizado.ui.leaders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.databinding.ItemLeaderboardsBinding
import com.heathkev.quizado.model.Result

class LeadersListAdapter : ListAdapter<Result, LeadersListAdapter.LeadersViewHolder>(DiffCallback){

    class LeadersViewHolder(private var binding: ItemLeaderboardsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.resultModel = result
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadersViewHolder {
        val view = ItemLeaderboardsBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return LeadersViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeadersViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.user_id == newItem.user_id
        }
    }
}