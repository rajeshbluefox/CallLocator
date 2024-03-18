package realapps.live.callerlocator.loginModule.apiFunctions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import realapps.live.callerlocator.loginModule.modalClass.GetUserInfoResponse
import realapps.live.callerlocator.loginModule.modalClass.RegisterUserResponse
import realapps.live.callerlocator.loginModule.modalClass.UserInfoData
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private var getUserInfoResponse = MutableLiveData<GetUserInfoResponse>()

    fun getUserInfo(phoneNumber: String) {
        viewModelScope.launch {
            getUserInfoResponse.postValue(loginRepository.getUserInfo(phoneNumber))
        }
    }

    fun resetUserInfo()
    {
        getUserInfoResponse = MutableLiveData<GetUserInfoResponse>()
    }

    fun getUserInfoResponse(): LiveData<GetUserInfoResponse> {
        return getUserInfoResponse
    }

    private var registerUserResponse = MutableLiveData<RegisterUserResponse>()

    fun postNewUser(userInfoData: UserInfoData) {
        viewModelScope.launch {
            registerUserResponse.postValue(loginRepository.postNewUser(userInfoData))
        }
    }

    fun postNewUserResponse(): LiveData<RegisterUserResponse> {
        return registerUserResponse
    }


}
