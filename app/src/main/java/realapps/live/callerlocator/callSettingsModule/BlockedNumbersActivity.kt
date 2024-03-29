package realapps.live.callerlocator.callSettingsModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.callSettingsModule.adapters.BlockedNumbersAdapter
import realapps.live.callerlocator.callSettingsModule.supportFunctions.BlockNumberDialog
import realapps.live.callerlocator.databinding.ActivityBlockedNumbersBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zDatabase.BlockedContactsDBHelper

@AndroidEntryPoint
class BlockedNumbersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockedNumbersBinding

    private lateinit var blockedNumbersAdapter: BlockedNumbersAdapter

    private lateinit var blockedNumbersDB: BlockedContactsDBHelper

    private lateinit var blockedNumbers : BlockNumberDialog

    private lateinit var dbHelper: BlockedContactsDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedNumbersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)


        blockedNumbersDB = BlockedContactsDBHelper(this)

        initViews()
        initBlockedNumberRv()
        onClickListeners()
    }

    fun initViews()
    {
        dbHelper = BlockedContactsDBHelper(this)

        blockedNumbers = BlockNumberDialog(layoutInflater,this,::addNumberToBlockList)
    }

    private fun addNumberToBlockList(phoneNumber: String)
    {
        val res = dbHelper.insertNumber(phoneNumber)
        if (res) {
            UtilFunctions.showToast(this, "Number Blocked Successfully")
            blockedNumbers.close()

            initBlockedNumberRv()
        }else{
            UtilFunctions.showToast(this, "Number Already Blocked")
            blockedNumbers.close()

        }
    }

    private fun initBlockedNumberRv() {

        val blockedNumbers = blockedNumbersDB.getAllNumbers()

        if (blockedNumbers.isNotEmpty()) {
            blockedNumbersAdapter = BlockedNumbersAdapter(
                this,
                blockedNumbers,
                ::onUnBlockClicked
            )
            binding.rvBlocketNumbers.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
                adapter = blockedNumbersAdapter
            }
        } else {
            UtilFunctions.showToast(this, "No Blocked Numbers")
        }

    }

    private fun onUnBlockClicked(number: String) {
        val status = blockedNumbersDB.deleteNumber(number)

        if (status) {
            UtilFunctions.showToast(this, "Number UnBlocked Successfully")
            initBlockedNumberRv()
        }

    }

    private fun onClickListeners() {
        binding.btBack.setOnClickListener {
            finish()
        }

        binding.btAddNumber.setOnClickListener {
            blockedNumbers.openBlockNumbersDialog()
        }


    }


}