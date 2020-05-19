/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.shared.utils

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.pr656d.shared.performance.PerformanceHelper

class FirebasePerformanceHelper : PerformanceHelper {

    private val firebasePerformance = FirebasePerformance.getInstance()

    private val traceStore = mutableMapOf<String, Trace>()

    fun getTrace(traceKey: String) = traceStore[traceKey]

    override fun startTrace(traceKey: String) {
        val trace = firebasePerformance.newTrace(traceKey)
        traceStore[traceKey] = trace
        trace.start()
    }

    override fun stopTrace(traceKey: String) {
        traceStore[traceKey]?.run {
            stop()
            traceStore.remove(traceKey)
        } ?: throw Exception("Trace does not started yet with ($traceKey) trace key")
    }

    override fun putAttribute(traceKey: String, attribute: Pair<String, String>) {
        traceStore[traceKey]!!.putAttribute(attribute.first, attribute.second)
    }

    override fun putMetric(traceKey: String, metric: Pair<String, Long>) {
        traceStore[traceKey]!!.putMetric(metric.first, metric.second)
    }
}