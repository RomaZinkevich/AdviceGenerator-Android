package com.example.adviceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.adviceapp.ui.theme.AdviceAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greeting()
        }
    }
}

data class SlipItem(var id:Int, var advice: String)
data class AdviceItem(var slip: SlipItem)


interface ApiService {
    @GET("advice")
    suspend fun fetchAdvice(): AdviceItem
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.adviceslip.com/"

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
    val apiService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

@Composable
fun Greeting() {
    var adviceItem = remember { mutableStateOf<AdviceItem>(AdviceItem(SlipItem(1, "Remember that spiders are more afraid of you, than you are of them.")))}
    var isLoading = remember { mutableStateOf(true)}
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        isLoading.value=true
        adviceItem.value = RetrofitInstance.apiService.fetchAdvice()
        isLoading.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "${adviceItem.value.slip.id}. ${adviceItem.value.slip.advice}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.sizeIn(minHeight = 80.dp, maxHeight = 120.dp)

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading.value=true
                coroutineScope.launch {
                    adviceItem.value = RetrofitInstance.apiService.fetchAdvice()
                    isLoading.value = false
                }
            },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "Fetch advice",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
