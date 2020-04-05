package com.pr656d.shared.data.user.info.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.NetworkHelper
import timber.log.Timber
import javax.inject.Inject

class UpdateFirestoreUserInfoDataSourceImpl @Inject constructor(
    private val authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val networkHelper: NetworkHelper
) : UpdateFirestoreUserInfoDataSource {

    private val result = MutableLiveData<Result<Unit>>()

    override fun updateUserInfo(userInfo: FirestoreUserInfo) {
        val uId = authIdDataSource.getUserId()

        // UId is required to work.
        if (uId == null) {
            Timber.d("Exception on update of UserInfoOnFirestore")
            result.postValue(
                Result.Error(IllegalStateException("User id not found"))
            )
            return
        }

        val task = firestore
            .collection(USERS_COLLECTION)
            .document(uId)
            .set(userInfo.asHashMap(), SetOptions.merge())

        if (networkHelper.isNetworkConnected()) {
            task
                .addOnSuccessListener {
                    result.postValue(Result.Success(Unit))
                }
                .addOnFailureListener {
                    Timber.d("Exception on update of UserInfoOnFirestore")
                    result.postValue(Result.Error(it))
                }
        } else {
            // Firebase offline persistence will reflect changes.
            result.postValue(Result.Success(Unit))
        }
    }

    private fun FirestoreUserInfo.asHashMap(): HashMap<String, Any?> =
        hashMapOf<String, Any?>().apply {
            put(KEY_FARM_NAME, getFarmName())
            put(KEY_DAIRY_CODE, getDairyCode())
            put(KEY_DAIRY_CUSTOMER_ID, getDairyCustomerId())
            put(KEY_FARM_ADDRESS, getFarmAddress())
            put(KEY_GENDER, getGender())
            put(KEY_DOB, getDateOfBirth())
            put(KEY_ADDRESS, getAddress())
        }


    override fun observeUpdateResult(): LiveData<Result<Unit>> {
        return result
    }

    companion object {
        const val USERS_COLLECTION = "users"
        const val KEY_FARM_NAME = "farmName"
        const val KEY_FARM_ADDRESS = "farmAddress"
        const val KEY_GENDER = "gender"
        const val KEY_DOB = "dateOfBirth"
        const val KEY_ADDRESS = "address"
        const val KEY_DAIRY_CODE = "dairyCode"
        const val KEY_DAIRY_CUSTOMER_ID = "dairyCustomerId"
    }
}