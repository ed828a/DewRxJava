package com.dew.edward.dewrxjavamvvm.model


/**
 * Created by Edward on 7/19/2018.
 */
data class ResultPageInfo(var prevPage: String = "",
                          var nextPage: String = "",
                          var totalResults: String = "",
                          var receivedItems: Int = 0) {

    fun reset() {
        prevPage = ""
        nextPage = ""
        totalResults = ""
        receivedItems = 0
    }
}

