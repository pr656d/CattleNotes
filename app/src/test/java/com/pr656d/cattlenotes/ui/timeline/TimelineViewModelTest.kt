package com.pr656d.cattlenotes.ui.timeline

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeBreedingRepository
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [TimelineViewModel].
 */
class TimelineViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createTimelineViewModel(
        loadTimelineUseCase: LoadTimelineUseCase = LoadTimelineUseCase(
            mock {
                on { getAllBreedingWithCattle() }
                    .doReturn(MutableLiveData(TestData.breedingWithCattleList))
            }
        ),
        updateBreedingUseCase: UpdateBreedingUseCase = UpdateBreedingUseCase(FakeBreedingRepository())
    ): TimelineViewModel {
        return TimelineViewModel(
            loadTimelineUseCase = loadTimelineUseCase,
            updateBreedingUseCase = updateBreedingUseCase
        )
    }

    @Test
    fun validateTimelineList() {
        val viewModel = createTimelineViewModel()

        val breedingWithCattleList = LiveDataTestUtil.getValue(viewModel.timelineList)
        assertThat(TestData.validTimelineList, isEqualTo(breedingWithCattleList))
    }

    object FailingUpdateBreedingUseCase : UpdateBreedingUseCase(FakeBreedingRepository()) {
        override fun execute(parameters: Breeding) {
            throw Exception("Error!")
        }
    }

    @Test
    fun saveBreedingCalled_verifySaved() {
        val newBreeding = TestData.breedingRHNegativePregnancyCheckNone

        val mockBreedingRepository = mock<BreedingRepository> {}

        val viewModel = createTimelineViewModel(
            updateBreedingUseCase = UpdateBreedingUseCase(mockBreedingRepository)
        )

        val selectedData = TimelineActionListener.ItemTimelineSaveData(
            BreedingWithCattle(
                TestData.breedingWithCattle1.cattle,
                newBreeding
            ),
            false
        )

        // Call on option selected.
        viewModel.saveBreeding(selectedData)

        // Verify update is called.
        verify(mockBreedingRepository, times(1)).updateBreeding(newBreeding)
    }

    @Test
    fun saveBreedingCalled_showMessageOnError() {
        val newBreeding = TestData.breedingRHNegativePregnancyCheckNone

        val viewModel = createTimelineViewModel(
            updateBreedingUseCase = FailingUpdateBreedingUseCase
        )

        val selectedData = TimelineActionListener.ItemTimelineSaveData(
            BreedingWithCattle(
                TestData.breedingWithCattle1.cattle,
                newBreeding
            ),
            false
        )

        // Save called
        viewModel.saveBreeding(selectedData)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }
}