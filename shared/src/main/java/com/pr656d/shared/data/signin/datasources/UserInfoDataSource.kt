package com.pr656d.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import com.pr656d.shared.data.signin.UserInfoOnFirestore
import com.pr656d.shared.domain.result.Result

interface UserInfoDataSource {
    /**
     * Listens to changes in the user document in Firestore. A Change in the "users/userId" fields
     * will emit a new user.
     */
    fun listenToUserChanges(userId: String)

    /**
     * Returns the holder of the result of listening to the data source.
     */
    fun observeResult(): LiveData<Result<UserInfoOnFirestore?>?>

    /**
     * Clear listeners and set the result of the observable to false when the user is not signed in.
     */
    fun removeUser()
}