package com.github.sanctuscorvus

data class HttpResponse<T>(val statusCode: Int, val body: T?)