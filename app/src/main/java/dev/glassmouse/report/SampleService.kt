package dev.glassmouse.report

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.reactivex.Single
import retrofit2.http.GET

interface SampleService {
    @GET("breeds/image/random")
    fun getDogImage(): Single<DogResponse>
}

data class DogResponse(
    @Json(name = "message") val message: String,
    @Json(name = "status") val status: String
)

class DogResponseAdapter : JsonAdapter<DogResponse>() {
    @FromJson
    override fun fromJson(reader: JsonReader): DogResponse? {
        val map = reader.readJsonValue() as Map<*, *>
        return DogResponse(message = map["message"] as String, status = map["status"] as String)
    }

    @ToJson
    override fun toJson(
        writer: JsonWriter,
        value: DogResponse?
    ) {
        value?.let {
            writer.beginObject()
            writer.name("message").value(it.message)
            writer.name("status").value(it.status)
            writer.endObject()
        }
    }
}

