package com.dew.edward.dewrxjavamvvm.data.repository

import io.reactivex.Scheduler


/**
 * Created by Edward on 7/19/2018.
 */
interface Scheduler {
    fun mainThread(): Scheduler
    fun io(): Scheduler
}