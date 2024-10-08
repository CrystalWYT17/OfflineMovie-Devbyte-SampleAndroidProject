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

package com.example.offlinemovie_devbyte.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.offlinemovie_devbyte.database.VideosDatabase
import com.example.offlinemovie_devbyte.database.asDomainModel
import com.example.offlinemovie_devbyte.domain.Video
import com.example.offlinemovie_devbyte.network.Network
import com.example.offlinemovie_devbyte.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideosRepository(private val database: VideosDatabase) {

    /**
     * A playlist of videos that can be shown on the screen
     */
    val videos: LiveData<List<Video>> = database.videoDao.getVideos().map { it.asDomainModel() }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [videos]
     */
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            val playlist = Network.devbytes.getPlaylist().await()
            database.videoDao.insertAll(*playlist.asDatabaseModel()) //* allows to pass in an array to a function that expects varargs.
        }
    }
}
