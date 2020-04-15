package com.pr656d.cattlenotes.ui.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.google.firebase.auth.UserInfo
import com.pr656d.cattlenotes.R
import com.pr656d.shared.data.user.info.FirebaseUserInfoDetailed
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.user.info.ObserveUserInfoDetailed
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * Interface to implement profile.
 *
 * You can inject a implementation of this via Dagger2, then use the implementation as an interface
 * delegate to add the functionality without writing any code
 *
 * Example usage:
 * ```
 * class MyViewModel @Inject constructor(
 *     profileDelegate: SetupProfileDelegate
 * ) : ViewModel(), ProfileDelegate by profileDelegate {
 * ```
 */
interface ProfileDelegate {

    /**
     * Provide current user info.
     */
    val currentUserInfo: LiveData<UserInfoDetailed?>

    val farmName: MediatorLiveData<String>

    val dairyCode: MediatorLiveData<String>

    val dairyCustomerId: MediatorLiveData<String>

    val farmAddress: MediatorLiveData<String>

    val imageUrl: MediatorLiveData<Uri?>

    val name: MediatorLiveData<String>

    // Not editable
    val email: LiveData<String?>

    // Not editable
    val phoneNumber: LiveData<String?>

    /**
     * For two way data binding for selection of gender.
     * Holds button id of selected button from group.
     */
    val selectedGenderId: MediatorLiveData<Int?>

    val dob: MediatorLiveData<LocalDate?>

    val address: MediatorLiveData<String>

    val updateUserInfoDetailedResult: LiveData<Result<Event<Pair<Result<Unit>, Result<Unit>>>>>

    val updateErrorMessage: LiveData<Int>

    /**
     * Convenience member holds saving state.
     */
    val savingProfile: MediatorLiveData<Boolean>

    fun saveProfile()
}

class ProfileDelegateImp @Inject constructor(
    observeUserInfoDetailed: ObserveUserInfoDetailed,
    private val updateUserInfoDetailedUseCase: UpdateUserInfoDetailedUseCase,
    private val networkHelper: NetworkHelper
) : ProfileDelegate {

    private val currentUserInfoResult = observeUserInfoDetailed.observe()

    override val currentUserInfo = currentUserInfoResult.map {
        (it as? Result.Success)?.data
    }

    override val farmName: MediatorLiveData<String> = MediatorLiveData()

    override val dairyCode: MediatorLiveData<String> = MediatorLiveData()

    override val dairyCustomerId: MediatorLiveData<String> = MediatorLiveData()

    override val farmAddress: MediatorLiveData<String> = MediatorLiveData()

    override val imageUrl: MediatorLiveData<Uri?> = MediatorLiveData()

    override val name: MediatorLiveData<String> = MediatorLiveData()

    override val email: LiveData<String?>
        get() = currentUserInfo.map {
            it?.getEmail()
        }

    override val phoneNumber: LiveData<String?>
        get() = currentUserInfo.map {
            it?.getPhoneNumber()
        }

    override val selectedGenderId: MediatorLiveData<Int?> = MediatorLiveData<Int?>()

    override val dob: MediatorLiveData<LocalDate?> = MediatorLiveData()

    override val address: MediatorLiveData<String> = MediatorLiveData()

    override val updateUserInfoDetailedResult: LiveData<Result<Event<Pair<Result<Unit>, Result<Unit>>>>> =
        updateUserInfoDetailedUseCase.observe()

    override val savingProfile =
        MediatorLiveData<Boolean>().apply { postValue(false) }

    private val _updateErrorMessage = MediatorLiveData<@StringRes Int>()
    override val updateErrorMessage: LiveData<Int>
        get() = _updateErrorMessage

    init {
        observeUserInfoDetailed.execute(Any())

        farmName.addSource(currentUserInfo) { user ->
            farmName.postValue(user?.getFarmName())
        }

        dairyCode.addSource(currentUserInfo) { user ->
            dairyCode.postValue(user?.getDairyCode())
        }

        dairyCustomerId.addSource(currentUserInfo) { user ->
            dairyCustomerId.postValue(user?.getDairyCustomerId())
        }

        farmAddress.addSource(currentUserInfo) { user ->
            farmAddress.postValue(user?.getFarmAddress())
        }

        imageUrl.addSource(currentUserInfo) { user ->
            imageUrl.postValue(user?.getPhotoUrl())
        }

        selectedGenderId.addSource(currentUserInfo) { user ->
            selectedGenderId.postValue(
                user?.getGender().getGenderId()
            )
        }

        dob.addSource(currentUserInfo) { user ->
            dob.postValue(
                user?.getDateOfBirth()?.toLongOrNull()?.let {
                    TimeUtils.toLocalDate(it)
                }
            )
        }

        address.addSource(currentUserInfo) { user ->
            address.postValue(user?.getAddress())
        }

        name.addSource(currentUserInfo) { user ->
            name.postValue(user?.getDisplayName())
        }

        savingProfile.addSource(updateUserInfoDetailedResult) {
            if (it is Result.Success || it is Result.Error)
                savingProfile.postValue(false)
        }

        _updateErrorMessage.addSource(updateUserInfoDetailedResult) { result ->
            (result as? Result.Success)?.data?.peekContent()?.let {
                if (it.first is Result.Error && it.second is Result.Error)
                    _updateErrorMessage.postValue(R.string.error_profile_change_not_saved)
                else if (it.first is Result.Error)
                    _updateErrorMessage.postValue(R.string.error_profile_change_incomplete)
                else if (it.second is Result.Error)
                    _updateErrorMessage.postValue(R.string.error_profile_change_incomplete)
            }
            (result as? Result.Error)?.exception?.let {
                _updateErrorMessage.postValue(R.string.error_unknown)
            }
        }
    }

    override fun saveProfile() {
        if (networkHelper.isNetworkConnected()) {
            updateUserInfoDetailedUseCase.execute(
                FirebaseUserInfoDetailed(
                    getFirebaseUserInfo(),
                    getUserInfoOnFirestore()
                )
            )
            savingProfile.postValue(true)
        } else {
            _updateErrorMessage.postValue(R.string.network_not_available)
        }
    }

    private fun getFirebaseUserInfo(): UserInfoBasic {
        return currentUserInfo.value!!.run {
            object : UserInfoBasic {
                override fun isSignedIn(): Boolean {
                    return isSignedIn()
                }

                override fun getEmail(): String? {
                    return getEmail()
                }

                override fun getProviderData(): MutableList<out UserInfo>? {
                    return getProviderData()
                }

                override fun getLastSignInTimestamp(): Long? {
                    return getLastSignInTimestamp()
                }

                override fun getCreationTimestamp(): Long? {
                    return getCreationTimestamp()
                }

                override fun isAnonymous(): Boolean? {
                    return isAnonymous()
                }

                override fun getPhoneNumber(): String? {
                    return getPhoneNumber()
                }

                override fun getUid(): String? {
                    return getUid()
                }

                override fun isEmailVerified(): Boolean? {
                    return isEmailVerified()
                }

                override fun getDisplayName(): String? {
                    return name.value
                }

                override fun getPhotoUrl(): Uri? {
                    return imageUrl.value
                }

                override fun getProviderId(): String? {
                    return getProviderId()
                }
            }
        }
    }

    private fun getUserInfoOnFirestore(): FirestoreUserInfo {
        return object : FirestoreUserInfo {
            override fun getFarmName(): String? {
                return farmName.value
            }

            override fun getFarmAddress(): String? {
                return farmAddress.value
            }

            override fun getGender(): String? {
                return selectedGenderId.value?.getGenderString()
            }

            override fun getDateOfBirth(): String? {
                return dob.value?.let { TimeUtils.toEpochMillis(it).toString() }
            }

            override fun getAddress(): String? {
                return address.value
            }

            override fun getDairyCode(): String? {
                return dairyCode.value
            }

            override fun getDairyCustomerId(): String? {
                return dairyCustomerId.value
            }
        }
    }

    fun Int.getGenderString(): String = when (this) {
        R.id.toggleButtonMale -> "Male"
        R.id.toggleButtonFemale -> "Female"
        R.id.toggleButtonOther -> "Other"
        else -> throw IllegalArgumentException("Invalid selection of gender")
    }

    private fun String?.getGenderId(): Int? = when (val string = this) {
        null -> null    // If null comes don't do anything.
        "Male" -> R.id.toggleButtonMale
        "Female" -> R.id.toggleButtonFemale
        "Other" -> R.id.toggleButtonOther
        else -> throw IllegalArgumentException("Could not convert gender string to toggle id. Invalid string $string")
    }
}