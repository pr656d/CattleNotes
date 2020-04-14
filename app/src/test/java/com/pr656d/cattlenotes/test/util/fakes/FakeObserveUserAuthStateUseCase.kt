package com.pr656d.cattlenotes.test.util.fakes

import com.nhaarman.mockito_kotlin.mock
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.auth.ObserveUserAuthStateUseCase
import com.pr656d.shared.domain.result.Result

class FakeObserveUserAuthStateUseCase(
    user: Result<UserInfoBasic?>? = Result.Success(mock()),
    firestoreUserInfo: Result<FirestoreUserInfo?>? = Result.Success(mock())
) : ObserveUserAuthStateUseCase(
    FakeAuthStateUserDataSource(user),
    FakeObserveFirestoreUserInfoDataSource(firestoreUserInfo)
)