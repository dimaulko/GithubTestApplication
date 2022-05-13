package com.nametag.githubtestapplication.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/repositories")
    fun searchRepo(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Single<PaginationResponse<Repo>>

    @GET("repos/{user}/{repo}/languages")
    fun getLanguagesForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ): Single<Map<String, Long>>

    @GET("repos/{user}/{repo}/contributors")
    fun getContributorsForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ): Single<List<Contributor>>
}
