package com.pr656d.cattlenotes.test.util.fakes

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.cattlenotes.ui.profile.ProfileDelegateImp
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import java.util.*

class FakeProfileDelegate(
    userInfoDetailed: UserInfoDetailed? = mock {
        on { isSignedIn() }.doReturn(true)
        on { getDisplayName() }.doReturn("Prem Patel")
        on { getEmail() }.doReturn("someone@something.com")
        on { getUid() }.doReturn(UUID.randomUUID().toString())
        on { getGender() }.doReturn("Male")
        on { getFarmName() }.doReturn("Some Name")
        on { getDairyCode() }.doReturn("1231")
        on { getDairyCustomerId() }.doReturn("9628276")
    },
    userInfoBasic: UserInfoBasic? = userInfoDetailed,
    firestoreUserInfo: FirestoreUserInfo? = userInfoDetailed,
    userInfoRepository: UserInfoRepository = FakeUserInfoRepository(),
    networkHelper: NetworkHelper = mock {
        on { isNetworkConnected() }.doReturn(true)
    }
) : ProfileDelegate by ProfileDelegateImp(
    FakeObserveUserAuthStateUseCase(
        Result.Success(userInfoBasic),
        Result.Success(firestoreUserInfo)
    ),
    UpdateUserInfoDetailedUseCase(userInfoRepository),
    networkHelper
)