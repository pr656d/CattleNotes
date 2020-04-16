package com.pr656d.cattlenotes.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeProfileDelegate
import com.pr656d.cattlenotes.test.util.fakes.FakeThemedActivityDelegate
import com.pr656d.cattlenotes.test.util.fakes.FakeUserInfoRepository
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.data.LoadDataUseCase
import com.pr656d.shared.domain.login.GetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetLoginCompletedUseCase
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.NetworkHelper
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
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

    private val mockDbLoader: DbLoader = mock {}

    private fun createLoginViewModel(
        preferenceStorage: PreferenceStorage = mock {
            on { loginCompleted }.doReturn(false)
            on { firstTimeProfileSetupCompleted }.doReturn(false)
        },
        userInfoRepository: UserInfoRepository = FakeUserInfoRepository(),
        networkHelper: NetworkHelper = mock {
            on { isNetworkConnected() }.doReturn(true)
        },
        dbLoader: DbLoader = mockDbLoader
    ): LoginViewModel {
        return LoginViewModel(
            FakeProfileDelegate(
                userInfoRepository = userInfoRepository,
                networkHelper = networkHelper
            ),
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
        currentUserInfo.observeForever { }
        farmName.observeForever { }
        dairyCode.observeForever { }
        dairyCustomerId.observeForever { }
        farmAddress.observeForever { }
        imageUrl.observeForever { }
        name.observeForever { }
        email.observeForever { }
        phoneNumber.observeForever { }
        dob.observeForever { }
        address.observeForever { }
        // LoginViewModel
        loginStatus.observeForever { }
        logginIn.observeForever { }
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

    @Test
    fun saveProfileCalled_navigateToMainScreenOnSuccess() {
        val viewModel = createLoginViewModel()

        // Call save profile
        viewModel.saveProfile()

        val launchMainScreen = LiveDataTestUtil.getValue(viewModel.launchMainScreen)
        assertThat(Unit, isEqualTo(launchMainScreen?.getContentIfNotHandled()))
    }

    @Test
    fun saveProfileCalledButNetworkNotAvailable_showUpdateErrorMessage() {
        val viewModel = createLoginViewModel(
            networkHelper = mock { on { isNetworkConnected() }.doReturn(false) }
        )

        // Call save profile
        viewModel.saveProfile()

        val updateErrorMessage = LiveDataTestUtil.getValue(viewModel.updateErrorMessage)
        assertNotNull(updateErrorMessage)
    }

    @Test
    fun updateResultBothError_showUpdateErrorMessage() {
        val viewModel = createLoginViewModel(
            userInfoRepository = FakeUserInfoRepository(
                Pair(Result.Error(Exception("Error!")), Result.Error(Exception("Error!")))
            )
        )

        // Call save profile
        viewModel.saveProfile()

        val updateErrorMessage = LiveDataTestUtil.getValue(viewModel.updateErrorMessage)
        assertNotNull(updateErrorMessage)
    }

    @Test
    fun updateResultFirstIsError_showUpdateErrorMessage() {
        val viewModel = createLoginViewModel(
            userInfoRepository = FakeUserInfoRepository(
                Pair(Result.Error(Exception("Error!")), Result.Success(Unit))
            )
        )

        // Call save profile
        viewModel.saveProfile()

        val updateErrorMessage = LiveDataTestUtil.getValue(viewModel.updateErrorMessage)
        assertNotNull(updateErrorMessage)
    }

    @Test
    fun updateResultSecondIsError_showUpdateErrorMessage() {
        val viewModel = createLoginViewModel(
            userInfoRepository = FakeUserInfoRepository(
                Pair(Result.Success(Unit), Result.Error(Exception("Error!")))
            )
        )

        // Call save profile
        viewModel.saveProfile()

        val updateErrorMessage = LiveDataTestUtil.getValue(viewModel.updateErrorMessage)
        assertNotNull(updateErrorMessage)
    }
}