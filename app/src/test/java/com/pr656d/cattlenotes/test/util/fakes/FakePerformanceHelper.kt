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

package com.pr656d.cattlenotes.test.util.fakes

import com.pr656d.shared.performance.PerformanceHelper

class FakePerformanceHelper : PerformanceHelper {
    override fun startTrace(traceKey: String) {}

    override fun stopTrace(traceKey: String) {}

    override fun putAttribute(traceKey: String, attribute: Pair<String, String>) {}

    override fun putMetric(traceKey: String, metric: Pair<String, Long>) {}
}