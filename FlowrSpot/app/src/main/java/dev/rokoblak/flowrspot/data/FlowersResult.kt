package dev.rokoblak.flowrspot.data


sealed class FlowersResult {
    class Success(val data: List<Flower>, val nextPageIndex: Int?) : FlowersResult()
    class Failure(val networkResult: NetworkResult) : FlowersResult()
}