package com.pr656d.cattlenotes.ui.timeline

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
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
        )
    ): TimelineViewModel {
        return TimelineViewModel(
            loadTimelineUseCase = loadTimelineUseCase
        )
    }

    @Test
    fun validateTimelineList() {
        val viewModel = createTimelineViewModel()

        val breedingWithCattleList = LiveDataTestUtil.getValue(viewModel.timelineList)
        assertThat(TestData.validTimelineList, isEqualTo(breedingWithCattleList))
    }
}