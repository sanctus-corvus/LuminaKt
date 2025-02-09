package com.github.sanctuscorvus

public data class HttpResponse<T>(val statusCode: Int, val body: T?)