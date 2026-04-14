package dev.set17.tftacademy.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class TftAcademyClient(private val httpClient: HttpClient) {

    suspend fun fetchTierListData(set: Int = 17): String {
        return httpClient
            .get("https://tftacademy.com/tierlist/comps/set$set/__data.json")
            .bodyAsText()
    }
}
