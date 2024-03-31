package com.example.calender.features.calendar.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import com.example.calender.databinding.MonthViewBinding
import java.util.Calendar
import java.util.LinkedList

class MonthView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

   private val grid:GridLayout
   var onDayClicked:((Int,Int,Int) -> Unit)? = null

   init {
     val binding = MonthViewBinding.inflate(LayoutInflater.from(context))
     addView(binding.root.apply {
         layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
     })
     grid = binding.calendarGridLayout
     grid.background = ColorDrawable(Color.LTGRAY )
       addWeekDaysRow()
       addDates()
   }


   private fun addDates(){
       val month = 3
       val year = 2023
       val dates = LinkedList<Int>()
       val currentDate = getCurrentDate()
       val startDay = getStartDay(month,year) + 1
       repeat(startDay){
           dates.add(0)
       }
       val endDate = getEndDateOfMonth(month,year)
       for(i in 1..endDate){
           dates.add(i)
       }
       dates.forEach {date ->
           grid.addView(
               Cell(context).apply {
               this.date = if(date == 0) "" else date.toString()
               setOnClickListener {
                   onDayClicked?.invoke(year,month,date)
               }
               if(currentDate == date){
                   rectColor = Color.GRAY
               }
               size = resources.displayMetrics.widthPixels/7
           })

       }
   }

   private fun getCurrentDate(): Int {
       val calendar = Calendar.getInstance()
       val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
       return dayOfMonth
   }

   private fun getEndDateOfMonth(month: Int,year:Int): Int {
       val calendar = Calendar.getInstance().apply {
           set(year, month, 1)
       }
       return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
   }

   private fun getStartDay(month: Int,year:Int): Int {
       val calendar = Calendar.getInstance().apply {
           set(year, month, 1)
       }
       return calendar.get(Calendar.DAY_OF_WEEK)
   }



   private fun addWeekDaysRow(){
      val weekdays = listOf("Sun","Mon","Tue","Web","Thu","Fri","Sat")
      weekdays.forEach {
          grid.addView(Cell(context).apply {
              text = it
              size = resources.displayMetrics.widthPixels/7
          })
      }
   }
}
