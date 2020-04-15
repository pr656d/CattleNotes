package com.pr656d.cattlenotes.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeObserveUserAuthStateUseCase
import com.pr656d.cattlenotes.test.util.fakes.FakeThemedActivityDelegate
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.cattlenotes.ui.profile.ProfileDelegateImp
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.data.LoadDataUseCase
import com.pr656d.shared.domain.login.GetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetLoginCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.*
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [LoginViewModel].
 */
class LoginViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private val mockUserInfoDetailed: UserInfoDetailed = mock {
        on { isSignedIn() }.doReturn(true)
        on { getDisplayName() }.doReturn("Prem Patel")
        on { getEmail() }.doReturn("someone@something.com")
        on { getUid() }.doReturn(UUID.randomUUID().toString())
        on { getGender() }.doReturn("Male")
        on { getFarmName() }.doReturn("Some Name")
        on { getDairyCode() }.doReturn("1231")
        on { getDairyCustomerId() }.doReturn("9628276")
    }

    private val mockUserInfoBasic: UserInfoBasic = mockUserInfoDetailed

    private val mockFirestoreUserInfo: FirestoreUserInfo = mockUserInfoDetailed

    private val mockPreferenceStorage: PreferenceStorage = mock {
        on { loginCompleted }.doReturn(false)
        on { firstTimeProfileSetupCompleted }.doReturn(false)
    }

    private val mockNetworkHelper: NetworkHelper = mock {
        on { isNetworkConnected() }.doReturn(true)
    }

    private val mockDbLoader: DbLoader = mock {  }

    private val userRepository: UserInfoRepository = object : UserInfoRepository {
        override fun updateUserInfo(userInfo: UserInfoDetailed) {}

        override fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>> {
            return MutableLiveData()
        }
    }

    private fun createProfileDelegate(
        userInfoBasic: UserInfoBasic?,
        firestoreUserInfo: FirestoreUserInfo?,
        userInfoRepository: UserInfoRepository,
        networkHelper: NetworkHelper
    ): ProfileDelegate {
        return ProfileDelegateImp(
            FakeObserveUserAuthStateUseCase(
                Result.Success(userInfoBasic),
                Result.Success(firestoreUserInfo)
            ),
            UpdateUserInfoDetailedUseCase(userInfoRepository),
            networkHelper
        )
    }

    private fun createLoginViewModel(
        userInfoBasic: UserInfoBasic? = mockUserInfoBasic,
        firestoreUserInfo: FirestoreUserInfo? = mockFirestoreUserInfo,
        preferenceStorage: PreferenceStorage = mockPreferenceStorage,
        userInfoRepository: UserInfoRepository = userRepository,
        networkHelper: NetworkHelper = mockNetworkHelper,
        profileDelegate: ProfileDelegate = createProfileDelegate(
            userInfoBasic = userInfoBasic,
            firestoreUserInfo = firestoreUserInfo,
            userInfoRepository = userInfoRepository,
            networkHelper = networkHelper
        ),
        dbLoader: DbLoader = mockDbLoader
    ): LoginViewModel {
        return LoginViewModel(
            profileDelegate,
            FakeThemedActivityDelegate(),
            GetFirstTimeProfileSetupCompletedUseCase(preferenceStorage),
            SetFirstTimeProfileSetupCompletedUseCase(preferenceStorage),
            networkHelper,
            SetLoginCompletedUseCase(preferenceStorage),
            LoadDataUseCase(dbLoader)
        ).apply { observeUnobserved() }
    }

    private fun LoginViewModel.observeUnobserved() {
        // Profile Delegate
        currentUserInfo.observeForever {  }
        farmName.observeForever {  }
        dairyCode.observeForever {  }
        dairyCustomerId.observeForever {  }
        farmAddress.observeForever {  }
        imageUrl.observeForever {  }
        name.observeForever {  }
        email.observeForever {  }
        phoneNumber.observeForever {  }
        dob.observeForever {  }
        address.observeForever {  }
        // LoginViewModel
        loginStatus.observeForever {  }
        logginIn.observeForever {  }
    }

    @Test
    fun loginClicked_launchFirebaseAuthUi() {
        val viewModel = createLoginViewModel()

        // Login clicked
        viewModel.onLoginClick()

        val launchFirebaseAuthUI = LiveDataTestUtil.getValue(viewModel.launchFirebaseAuthUI)
        assertThat(Unit, isEqualTo(launchFirebaseAuthUI?.getContentIfNotHandled()))
    }

    @Test
    fun loginClicked_logginInIsTrue() {
        val viewModel = createLoginViewModel()

        // Login clicked
        viewModel.onLoginClick()

        val logginIn = LiveDataTestUtil.getValue(viewModel.logginIn)!!
        assertTrue(logginIn)
    }

    @Test
    fun firstTimeProfileSetupNotCompletedButLoginCompleted_launchSetupProfileScreen() {
        val viewModel = createLoginViewModel(
            preferenceStorage = mock {
                on { loginCompleted }.doReturn(true)
                on { firstTimeProfileSetupCompleted }.doReturn(false)
            }
        )

        val launchSetupProfileScreen = LiveDataTestUtil.getValue(viewModel.launchSetupProfileScreen)
        assertThat(Unit, isEqualTo(launchSetupProfileScreen?.getContentIfNotHandled()))
    }

    @Test
    fun onLoginSuccessIsNewUser_launchSetupProfileScreen() {
        val viewModel = createLoginViewModel()

        // Login success
        viewModel.onLoginSuccess(true)

        val launchSetupProfileScreen = LiveDataTestUtil.getValue(viewModel.launchSetupProfileScreen)
        assertThat(Unit, isEqualTo(launchSetupProfileScreen?.getContentIfNotHandled()))
    }

    @Test
    fun onLoginSuccessIsExistingUser_launchMainScreen() {
        val viewModel = createLoginViewModel()

        // Login success
        viewModel.onLoginSuccess(false)

        val launchMainScreen = LiveDataTestUtil.getValue(viewModel.launchMainScreen)
        assertThat(Unit, isEqualTo(launchMainScreen?.getContentIfNotHandled()))
    }

    @Test
    fun onLoginSuccessIfExistingUser_loadData() {
        val viewModel = createLoginViewModel()

        // Login success
        viewModel.onLoginSuccess(false)

        // Verify load is called.
        verify(mockDbLoader).load()
    }
}