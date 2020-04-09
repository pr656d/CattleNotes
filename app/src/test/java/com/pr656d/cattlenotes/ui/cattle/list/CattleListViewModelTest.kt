package com.pr656d.cattlenotes.ui.cattle.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.list.LoadCattleListUseCase
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [CattleListViewModel]
 */
class CattleListViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Test
    fun cattleListIsLoaded() {
        val fakeCattleRepository = object : FakeCattleRepository() {
            override fun getObservableAllCattle(): LiveData<List<Cattle>> {
                val result = MutableLiveData<List<Cattle>>()
                result.postValue(TestData.cattleList)
                return result
            }
        }

        val loadObservableCattleListUseCase = LoadCattleListUseCase(fakeCattleRepository)

        val viewModel = CattleListViewModel(loadObservableCattleListUseCase)

        val cattleList = LiveDataTestUtil.getValue(viewModel.cattleList)
        assertThat(cattleList, isEqualTo(TestData.cattleList))
    }

    @Test
    fun openCattleIsCalled_launchCattleDetail() {
        val fakeCattleRepository = object : FakeCattleRepository() {
            override fun getObservableAllCattle(): LiveData<List<Cattle>> {
                val result = MutableLiveData<List<Cattle>>()
                result.postValue(TestData.cattleList)
                return result
            }
        }

        val loadObservableCattleListUseCase = LoadCattleListUseCase(fakeCattleRepository)

        val viewModel = CattleListViewModel(loadObservableCattleListUseCase)

        // Take any random cattle from list
        val cattle = TestData.cattleList.random()

        // Call function to open cattle
        viewModel.openCattle(cattle)

        val launchCattleDetail = LiveDataTestUtil.getValue(viewModel.launchCattleDetail)
        assertThat(cattle, isEqualTo(launchCattleDetail?.getContentIfNotHandled()))
    }

    @Test
    fun addCattleIsCalled_launchAddCattle() {
        val fakeCattleRepository = FakeCattleRepository()

        val loadObservableCattleListUseCase = LoadCattleListUseCase(fakeCattleRepository)

        val viewModel = CattleListViewModel(loadObservableCattleListUseCase)

        // Call function to add cattle
        viewModel.addCattle()

        val launchAddCattleScreen = LiveDataTestUtil.getValue(viewModel.launchAddCattleScreen)
        assertThat(Unit, isEqualTo(launchAddCattleScreen?.getContentIfNotHandled()))
    }

    @Test
    fun cattleListIsEmpty_isEmptyShouldBeTrue() {
        val fakeCattleRepository = object : FakeCattleRepository() {
            override fun getObservableAllCattle(): LiveData<List<Cattle>> {
                return MutableLiveData<List<Cattle>>(emptyList())
            }
        }

        val loadObservableCattleListUseCase = LoadCattleListUseCase(fakeCattleRepository)

        val viewModel = CattleListViewModel(loadObservableCattleListUseCase)

        val isEmpty = LiveDataTestUtil.getValue(viewModel.isEmpty) ?: true
        assertThat(isEmpty, isEqualTo(true))
    }

    @Test
    fun cattleListIsAvailable_isEmptyShouldBeFalse() {
        val fakeCattleRepository = object : FakeCattleRepository() {
            override fun getObservableAllCattle(): LiveData<List<Cattle>> {
                val result = MutableLiveData<List<Cattle>>()
                result.postValue(TestData.cattleList)
                return result
            }
        }

        val loadObservableCattleListUseCase = LoadCattleListUseCase(fakeCattleRepository)

        val viewModel = CattleListViewModel(loadObservableCattleListUseCase)

        val isEmpty = LiveDataTestUtil.getValue(viewModel.isEmpty)!!
        assertFalse(isEmpty)
    }
}