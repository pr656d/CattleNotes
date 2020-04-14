package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.result.Result

class FakeAuthStateUserDataSource(
    private val user: Result<UserInfoBasic?>?
) : AuthStateUserDataSource {
    override fun startListening() {}

    override fun getBasicUserInfo(): LiveData<Result<UserInfoBasic?>> {
        return MutableLiveData<Result<UserInfoBasic?>>().apply { value = user }
    }

    override fun clearListener() {}

    override fun reload() {}
}