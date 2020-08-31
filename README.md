# Cattle Notes

[![Travis](https://travis-ci.com/pr656d/CattleNOTES.svg?token=XKzGx5G9zSSfxy3fjRts&branch=master)](https://travis-ci.com/pr656d/CattleNOTES) [![Github Actions Unit Test](https://github.com/pr656d/CattleNotes/workflows/Android%20Unit%20Test/badge.svg)](https://github.com/pr656d/CattleNotes/actions?query=workflow%3A%22Android+Unit+Test%22) [![Github Actions Validate Gradle Wrapper](https://github.com/pr656d/CattleNotes/workflows/Validate%20Gradle%20Wrapper/badge.svg)](https://github.com/pr656d/CattleNotes/actions?query=workflow%3A%22Validate+Gradle+Wrapper%22) [![Bitrise](https://app.bitrise.io/app/55efcf038c3d0627/status.svg?token=h1tuSpdLMFKzyiPh9ThjNg&branch=main)](https://app.bitrise.io/app/55efcf038c3d0627)

Provides an all-in-one-place for efficient monitoring of a cattle farm, and assists farmers to easily
track the breeding activities of their cattle. The app auto-records as well as allows the user to
manually add the milk details.

# Features

1. **Cattle**
    - Store cattle information like tag number, name, breed, type, group, date of birth, etc.

    <div style="padding : 20px">
    <img align="center" src="screenshots/cattle.png" alt="Cattle Screen" width="255" height="520">
    <img align="center" src="screenshots/cattleAdd.png" alt="Add cattle" width="255" height="520">
    <img align="center" src="screenshots/cattleDetail.png" alt="Cattle Detail" width="255" height="520">
    </div>

2. **Breeding**
    - Store breeding information for each and every cattle.
    - Get breeding reminders on desired time of the breeding day.

    <div style="padding : 20px">
    <img align="center" src="screenshots/breedingAdd.png" alt="Breeding Screen" width="255" height="520">
    <img align="center" src="screenshots/breedingAdd2.png" alt="Add breeding expanded" width="255" height="520">
    <img align="center" src="screenshots/breedingHistory.png" alt="Breeding History" width="255" height="520">
    </div>

3. **Timeline**
    - Shows next breeding events.
    
    <div style="padding : 20px">
    <img align="center" src="screenshots/timeline.png" alt="Timeline Screen" width="255" height="520">
    </div>

4. **Milking**
    - **Automatic Milk Collection**
        - Saves milk information whenever SMS is received.
        - **Supported milk SMS senders**
            - BGAMAMCS (Amul)

        <div style="padding : 20px">
        <img align="center" src="screenshots/milking.png" alt="Timeline Screen" width="255" height="520">
        </div>

    - **Manual**
        - Add and delete milk information manually.

        <div style="padding : 20px">
        <img align="center" src="screenshots/milkingAddManual.png" alt="Timeline Screen" width="255" height="520">
        </div>

4. **User profile**

5. **Data backup and restore**

# Tech stack

- [Android Jetpack](https://developer.android.com/jetpack/)
    - Foundation
        - Android KTX
        - App Compat
        - Multidex
    - Architecture
        - Data Binding
        - Lifecycles
        - LiveData
        - Navigation
        - Room
        - ViewModel
        - WorkManager
    - Behaviour
        - Permissions

- [Firebase](https://firebase.google.com/)

- [Dagger-android](https://developer.android.com/training/dependency-injection/dagger-android)

- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)

- [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html)

- [MVVM Architecture](https://developer.android.com/jetpack/docs/guide)

- [Modularization](https://medium.com/google-developer-experts/modularizing-android-applications-9e2d18f244a0)

- [Auth UI](https://firebase.google.com/docs/auth/android/firebaseui)

- [Mockito](https://github.com/mockito/mockito)

- [Espresso](https://developer.android.com/training/testing/espresso/)

- [Travis](http://travis-ci.com/)

- [Github Actions](https://github.com/features/actions)

# Development Environment

The app is written entirely in Kotlin and uses the Kotlin gradle DSL build system.

# Setup

- Create new project on [Firebase](https://firebase.google.com/) and add android app with package name as "com.pr656d.cattlenotes". 
- Add google-services.json file at app level.

# Copyright

    Copyright (c) 2020 Cattle Notes. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
