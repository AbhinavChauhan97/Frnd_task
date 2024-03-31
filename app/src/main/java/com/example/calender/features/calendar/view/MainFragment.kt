package com.example.calender.features.calendar.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.calender.R
import com.example.calender.features.calendar.model.Task
import com.example.calender.databinding.FragmentMainBinding
import com.example.calender.features.calendar.viewmodel.CalendarViewModel
import com.example.calender.features.calendar.common.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: CalendarViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        observeEvents()
        with(binding) {
            monthView.onDayClicked = { year, month, day ->
                showAddTaskDialog(day, month, year)
            }
            viewsTasksButton.setOnClickListener {
                val action = MainFragmentDirections.actionMainFragmentToTasksFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun observeEvents(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.taskStatusEventSharedFlow.collect { event ->
                when(event){
                    is CalendarViewModel.TaskStatusEvent.TaskAdded  -> {
                        showToast(event.message)
                    }
                    is CalendarViewModel.TaskStatusEvent.FailedToAddTask -> {
                        showToast(event.reason)
                    }
                }
            }
        }
    }

    private fun showAddTaskDialog(day:Int,month:Int,year:Int){
        val customView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val editText = customView.findViewById<EditText>(R.id.editText)

        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Enter Task Details")
            .setView(customView)
            .setPositiveButton("OK") { dialog, which ->
                viewModel.addTask(
                    Task(
                    title = "$day/$month/$year",
                    description = editText.text.toString()
                )
                )
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

}