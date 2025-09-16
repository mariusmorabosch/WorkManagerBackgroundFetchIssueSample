package dev.glassmouse.report

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.RxWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.squareup.moshi.Moshi
import dev.glassmouse.report.ui.theme.BackgroundNetworkSampleTheme
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackgroundNetworkSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onPause() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val request = OneTimeWorkRequestBuilder<AppEventsUploadWorker>()
            .setConstraints(constraints)
            .setInitialDelay(20, TimeUnit.SECONDS)
            .build()

        println("@@: Enqueueing work")
        WorkManager.getInstance(this)
            .enqueueUniqueWork("background_call_test", ExistingWorkPolicy.KEEP, request)

        super.onPause()
    }
}

class AppEventsUploadWorker(appContext: Context, workerParams: WorkerParameters) :
    RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        val moshi = Moshi.Builder().add(DogResponseAdapter()).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val service: SampleService = retrofit.create(SampleService::class.java)

        return service.getDogImage().doOnSubscribe {
            println("@@: Calling api from background")
        }.map {
            println("@@: Received image URL: $it")
            Result.success()
        }.onErrorResumeNext { error ->
            println("@@: Error fetching from background: $error")
            Single.just(Result.failure())
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BackgroundNetworkSampleTheme {
        Greeting("Android")
    }
}