package com.nametag.githubtestapplication.data.network

import io.reactivex.Single
import retrofit2.http.Path

interface IRemoteRepo {
    fun searchRepo(
        query: String,
        sort: String,
        perPage: Int,
        page: Int
    ): Single<PaginationResponse<Repo>>

    fun getLanguagesForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ):Single<Map<String, Long>>

    fun getContributorsForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ):Single<List<Contributor>>
}