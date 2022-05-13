package com.nametag.githubtestapplication.data.providers

import com.nametag.githubtestapplication.data.network.Contributor
import com.nametag.githubtestapplication.data.network.IRemoteRepo
import com.nametag.githubtestapplication.data.network.PaginationResponse
import com.nametag.githubtestapplication.data.network.Repo
import com.nametag.githubtestapplication.data.preferences.IPrefDataSource
import io.reactivex.Single

class GithubProviderImpl(
    private val remoteRepoImpl: IRemoteRepo,
    private val localRepoImpl: IPrefDataSource,
) : IGithubProvider {
    override fun searchRepo(
        query: String,
        sort: String,
        perPage: Int,
        page: Int
    ): Single<PaginationResponse<Repo>> {
        return remoteRepoImpl.searchRepo(query, sort, perPage, page)
    }

    override fun getLanguagesForRepos(
        userName: String,
        repoName: String
    ): Single<Map<String, Long>> {
        return remoteRepoImpl.getLanguagesForRepos(userName, repoName)
    }

    override fun getContributorsForRepos(
        userName: String,
        repoName: String
    ): Single<List<Contributor>> {
        return remoteRepoImpl.getContributorsForRepos(userName, repoName)
    }
}