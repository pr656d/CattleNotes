package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result

class FakeUserInfoRepository(
    private val updateResult: Pair<Result<Unit>, Result<Unit>> =
        Pair(Result.Success(Unit), Result.Success(Unit))
) : UserInfoRepository {
    private val result = MutableLiveData<Event<Pair<Result<Unit>, Result<Unit>>>>()

    override fun updateUserInfo(userInfo: UserInfoDetailed) {
        result.postValue(Event(updateResult))
    }

    override fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>> {
        return result
    }
}