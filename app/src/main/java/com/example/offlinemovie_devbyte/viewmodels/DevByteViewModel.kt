/*
 * Copyright 2018, The Android Open Source Project
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
 *
 */

package com.example.offlinemovie_devbyte.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.offlinemovie_devbyte.database.getDatabase
import com.example.offlinemovie_devbyte.domain.Video
import com.example.offlinemovie_devbyte.network.Network
import com.example.offlinemovie_devbyte.network.asDomainModel
import com.example.offlinemovie_devbyte.repository.VideosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class DevByteViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     *This is main scope for all coroutines launched by ViewModel.
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val videoRespository = VideosRepository(database)

    init {
        viewModelScope.launch {
            videoRespository.refreshVideos()
        }
    }

    val playlist = videoRespository.videos

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DevByteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DevByteViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
