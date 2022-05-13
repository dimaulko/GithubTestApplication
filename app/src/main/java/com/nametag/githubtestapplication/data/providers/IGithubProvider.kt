package com.nametag.githubtestapplication.data.providers

import com.nametag.githubtestapplication.data.network.Contributor
import com.nametag.githubtestapplication.data.network.PaginationResponse
import com.nametag.githubtestapplication.data.network.Repo
import io.reactivex.Single
import retrofit2.http.Path

interface IGithubProvider {
    fun searchRepo(
        query: String,
        sort: String,
        perPage: Int,
        page: Int
    ): Single<PaginationResponse<Repo>>

    fun getLanguagesForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ): Single<Map<String, Long>>

    fun getContributorsForRepos(
        @Path("user") userName: String,
        @Path("repo") repoName: String
    ): Single<List<Contributor>>
}