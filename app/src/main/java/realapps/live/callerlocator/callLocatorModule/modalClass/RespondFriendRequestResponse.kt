package realapps.live.callerlocator.callLocatorModule.modalClass

import com.google.gson.annotations.SerializedName


data class RespondFriendRequestResponse(
    @SerializedName("status")
    val code: Int? = null,
    @SerializedName("message")
    val message: String? = null
)
