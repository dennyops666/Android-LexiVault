package com.example.lexivault.data.network

import com.example.lexivault.data.network.error.NetworkError
import com.example.lexivault.data.network.error.NetworkErrorHandler
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorHandlerTest {
    
    private val errorHandler = NetworkErrorHandler()
    
    @Test
    fun `test handle IOException`() {
        // Given
        val exception = IOException("Network error")
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.Connection::class.java)
        assertThat(error.message).contains("网络连接失败")
    }
    
    @Test
    fun `test handle SocketTimeoutException`() {
        // Given
        val exception = SocketTimeoutException("Timeout")
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.Timeout::class.java)
        assertThat(error.message).contains("请求超时")
    }
    
    @Test
    fun `test handle UnknownHostException`() {
        // Given
        val exception = UnknownHostException("Unknown host")
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.UnknownHost::class.java)
        assertThat(error.message).contains("无法连接到服务器")
    }
    
    @Test
    fun `test handle HttpException with 401`() {
        // Given
        val response = Response.error<String>(401, okhttp3.ResponseBody.create(null, ""))
        val exception = HttpException(response)
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.Unauthorized::class.java)
        assertThat(error.message).contains("未授权")
    }
    
    @Test
    fun `test handle HttpException with 404`() {
        // Given
        val response = Response.error<String>(404, okhttp3.ResponseBody.create(null, ""))
        val exception = HttpException(response)
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.NotFound::class.java)
        assertThat(error.message).contains("资源不存在")
    }
    
    @Test
    fun `test handle HttpException with 500`() {
        // Given
        val response = Response.error<String>(500, okhttp3.ResponseBody.create(null, ""))
        val exception = HttpException(response)
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.Server::class.java)
        assertThat(error.message).contains("服务器错误")
    }
    
    @Test
    fun `test handle unknown exception`() {
        // Given
        val exception = RuntimeException("Unknown error")
        
        // When
        val error = errorHandler.handleError(exception)
        
        // Then
        assertThat(error).isInstanceOf(NetworkError.Unknown::class.java)
        assertThat(error.message).contains("未知错误")
    }
}
