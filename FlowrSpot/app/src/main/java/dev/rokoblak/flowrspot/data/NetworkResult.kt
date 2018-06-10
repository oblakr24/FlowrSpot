package dev.rokoblak.flowrspot.data


sealed class NetworkResult {
    object Success : NetworkResult()
    object NoData : NetworkResult()
    object Timeout : NetworkResult()
    object NoNetwork : NetworkResult()
    data class Error(val throwable: Throwable) : NetworkResult()
}