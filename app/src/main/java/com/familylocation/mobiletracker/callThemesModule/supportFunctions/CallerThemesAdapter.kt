package com.familylocation.mobiletracker.callThemesModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.familylocation.mobiletracker.callThemesModule.modalClass.ThemesData
import com.familylocation.mobiletracker.databinding.ItemCallerThemeBinding


class CallerThemesAdapter(
    private var context: Context,
    private var themesList: ArrayList<ThemesData>,
    private val onThemeClicked: () -> Unit,
) :
    RecyclerView.Adapter<CallerThemesAdapter.CallerThemesAdapterViewHolder>() {


    class CallerThemesAdapterViewHolder(var binding: ItemCallerThemeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CallerThemesAdapterViewHolder {
        return CallerThemesAdapterViewHolder(
            ItemCallerThemeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: CallerThemesAdapterViewHolder, position: Int) {

        val themeItem = themesList[position]

        Glide.with(context)
            .load(themeItem.thumbNail)
            .fitCenter()
            .into(holder.binding.ivThemeThumbnail)

        holder.binding.themeLt.setOnClickListener {
            SelectedTheme.selectedTheme=themeItem
            SelectedTheme.selectedThemeCount = position + 1
            onThemeClicked.invoke()
        }


    }

    override fun getItemCount(): Int {
        return themesList.size
    }

}