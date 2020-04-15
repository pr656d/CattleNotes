package com.pr656d.cattlenotes.test.util.fakes

import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.auth.ObserveUserAuthStateUseCase
import com.pr656d.shared.domain.result.Result

class FakeObserveUserAuthStateUseCase(
    user: Result<UserInfoBasic?>?,
    firestoreUserInfo: Result<FirestoreUserInfo?>?
) : ObserveUserAuthStateUseCase(
    FakeAuthStateUserDataSource(user),
    FakeObserveFirestoreUserInfoDataSource(firestoreUserInfo)
)