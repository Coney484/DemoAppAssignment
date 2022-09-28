package com.example.demoapp


data class UploadResponse(
    val success: Boolean,
    val statusCode: Int,
    val error: Boolean,
    val message: String
)
