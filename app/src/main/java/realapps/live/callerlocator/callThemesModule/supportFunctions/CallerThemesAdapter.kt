package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import realapps.live.callerlocator.databinding.ItemCallerThemeBinding
import realapps.live.callerlocator.zCommonFuntions.Contact


class CallerThemesAdapter(
    private var context: Context,
    private var contactList: ArrayList<Contact>,
    private var themes: ArrayList<Int>,
    private val onContactClicked: (contact: Contact) -> Unit,
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

        val contact = contactList[position]

        var theme = 0

        if (position <= 3)
            theme = themes[position]

        holder.binding.tvCount.text = position.toString()


        if (position <= 3) {

            holder.binding.lottieAnimationView.setAnimation(
                theme
            )
        }
//        holder.binding.lottieAnimationView.tag = themeItem.jsonFileName // You can use this tag to identify the JSON file later if needed


        holder.binding.themeLt.setOnClickListener {
            SelectedTheme.selectedThemeCount = position+1
            onContactClicked.invoke(contact)
        }


    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}