/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.google.firebase.auth.UserInfo
import com.pr656d.cattlenotes.R
import com.pr656d.shared.data.user.info.FirebaseUserInfoDetailed
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.domain.user.info.ObserveUserInfoDetailed
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
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
    /**  Provide current user info.  */
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

    val updateErrorMessage: LiveData<Int>

    /**
     * Convenience member holds saving state.
     */
    val savingProfile: MutableLiveData<Boolean>

    suspend fun saveProfile(): Result<Event<Pair<Result<Unit>, Result<Unit>>>>
}

@ExperimentalCoroutinesApi
class ProfileDelegateImp @Inject constructor(
    observeUserInfoDetailed: ObserveUserInfoDetailed,
    private val updateUserInfoDetailedUseCase: UpdateUserInfoDetailedUseCase,
    private val networkHelper: NetworkHelper
) : ProfileDelegate {

    override val currentUserInfo = observeUserInfoDetailed(Unit)
        .map { it.successOr(null) }
        .asLiveData()

    override val farmName: MediatorLiveData<String> = MediatorLiveData()

    override val dairyCode: MediatorLiveData<String> = MediatorLiveData()

    override val dairyCustomerId: MediatorLiveData<String> = MediatorLiveData()

    override val farmAddress: MediatorLiveData<String> = MediatorLiveData()

    override val imageUrl: MediatorLiveData<Uri?> = MediatorLiveData()

    override val name: MediatorLiveData<String> = MediatorLiveData()

    override val email: LiveData<String?>
        get() = currentUserInfo.map { it?.getEmail() }

    override val phoneNumber: LiveData<String?>
        get() = currentUserInfo.map { it?.getPhoneNumber() }

    override val selectedGenderId: MediatorLiveData<Int?> = MediatorLiveData<Int?>()

    override val dob: MediatorLiveData<LocalDate?> = MediatorLiveData()

    override val address: MediatorLiveData<String> = MediatorLiveData()

    override val savingProfile = MutableLiveData(false)

    private val _updateErrorMessage = MutableLiveData<@StringRes Int>()
    override val updateErrorMessage: LiveData<Int>
        get() = _updateErrorMessage

    init {
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
                user?.getDateOfBirth()?.toLongOrNull()?.toLocalDate()
            )
        }

        address.addSource(currentUserInfo) { user ->
            address.postValue(user?.getAddress())
        }

        name.addSource(currentUserInfo) { user ->
            name.postValue(user?.getDisplayName())
        }
    }

    override suspend fun saveProfile(): Result<Event<Pair<Result<Unit>, Result<Unit>>>> {
        return if (networkHelper.isNetworkConnected()) {
            savingProfile.postValue(true)

            val result = updateUserInfoDetailedUseCase(
                FirebaseUserInfoDetailed(
                    getFirebaseUserInfo(),
                    getUserInfoOnFirestore()
                )
            )

            result.updateErrorMessage()

            savingProfile.postValue(false)

            result
        } else {
            _updateErrorMessage.postValue(R.string.network_not_available)
            Result.Error(Exception("No internet"))
        }
    }

    private fun Result<Event<Pair<Result<Unit>, Result<Unit>>>>.updateErrorMessage() {
        // Handle the event but dont consume it. Leave it for caller.
        successOr(null)?.peekContent()?.let {
            if (it.first is Result.Error && it.second is Result.Error)
                _updateErrorMessage.value = R.string.error_profile_change_not_saved
            else if (it.first is Result.Error)
                _updateErrorMessage.value = R.string.error_profile_change_incomplete
            else if (it.second is Result.Error)
                _updateErrorMessage.value = R.string.error_profile_change_incomplete
        }

        (this as? Result.Error)?.exception?.let {
            _updateErrorMessage.value = R.string.error_unknown
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
                return dob.value?.toLong().toString()
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