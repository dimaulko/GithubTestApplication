package com.nametag.githubtestapplication.data.network

import com.nametag.githubtestapplication.utils.rxParseErrorSingle
import io.reactivex.Single

class RemoteRepoImpl(private val apiService: ApiService) : IRemoteRepo {
    override fun searchRepo(
        query: String,
        sort: String,
        perPage: Int,
        page: Int
    ): Single<PaginationResponse<Repo>> {
        return apiService.searchRepo(query, sort, perPage, page)
            .onErrorResumeNext(rxParseErrorSingle())
    }

    override fun getLanguagesForRepos(
        userName: String,
        repoName: String
    ): Single<Map<String, Long>> {
        return apiService.getLanguagesForRepos(userName, repoName)
            .onErrorResumeNext(rxParseErrorSingle())
    }

    override fun getContributorsForRepos(
        userName: String,
        repoName: String
    ): Single<List<Contributor>> {
        return apiService.getContributorsForRepos(userName, repoName)
            .onErrorResumeNext(rxParseErrorSingle())
    }
}