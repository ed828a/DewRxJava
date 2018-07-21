package com.dew.edward.dewrxjavamvvm.model


/**
 * Created by Edward on 7/19/2018.
 */
data class QueryData(var queryString: String = "",
                     var type: QueryType = QueryType.QUERY_STRING,
                     var isInitializer: Boolean = true)

enum class QueryType {
    QUERY_STRING,
    RELATED_VIDEO_ID
}