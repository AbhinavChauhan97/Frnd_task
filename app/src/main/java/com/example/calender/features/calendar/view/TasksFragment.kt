package com.example.calender.features.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calender.R
import com.example.calender.databinding.FragmentTasksBinding
import com.example.calender.databinding.ItemTaskBinding
import com.example.calender.features.calendar.model.Task
import com.example.calender.features.calendar.viewmodel.CalendarViewModel
import com.example.calender.features.calendar.common.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    val viewModel: CalendarViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        observeEvents(binding)
        setupRecyclerView(binding.recylcerview)
    }

    private fun observeEvents(binding: FragmentTasksBinding) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.taskScreenEventSharedFlow.collect { event ->
                when(event){
                    is CalendarViewModel.TasksScreenEvents.OnTasksLoaded -> {
                        (binding.recylcerview.adapter as TasksAdapter).submitList(event.tasks)
                    }
                    is CalendarViewModel.TasksScreenEvents.OnTaskLoadFailed -> {
                        showToast(event.reason)
                    }
                    is CalendarViewModel.TasksScreenEvents.OnTaskDeleted -> {
                        (binding.recylcerview.adapter as TasksAdapter).submitList(event.remainingTasks)
                        showToast(event.message)
                    }
                    is CalendarViewModel.TasksScreenEvents.OnTaskDeletionFailed -> {
                       showToast(event.reason)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(recylcerview: RecyclerView) {
        with(recylcerview){
            layoutManager = LinearLayoutManager(context)
            adapter = TasksAdapter().apply {
                viewModel.loadTasks()
            }
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.deleteTask(viewModel.tasks[position])
                }
            })
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    class TasksAdapter : ListAdapter<Task, TasksAdapter.TaskViewHolder>(diffCallback){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return TaskViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

       class TaskViewHolder(val binding:ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){

           fun bind(item: Task){
               with(binding){
                  task = item
                  executePendingBindings()
               }
           }

       }

       companion object DiffCallback {
           val diffCallback = object : DiffUtil.ItemCallback<Task>(){
               override fun areItemsTheSame(oldItem: Task, newItem: Task) = newItem.id == oldItem.id

               override fun areContentsTheSame(oldItem: Task, newItem: Task) = newItem == oldItem

           }
       }


    }
}