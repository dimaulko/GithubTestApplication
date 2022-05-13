package com.nametag.githubtestapplication.data.network

import com.google.gson.annotations.SerializedName

data class Repo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val repoFullName: String,
    @SerializedName("owner") val owner: RepoOwner,
    @SerializedName("watchers") val watchers: Int,
    @SerializedName("forks_count") val forksCount: Int,
    @SerializedName("open_issues_count") val openIssuesCount: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("pushed_at") val pushedAt: String,
)
fun Repo.getRepoName():String{
    return repoFullName.split("/")[0]
}

data class RepoOwner(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class Contributor(
    @SerializedName("id") val id: Int,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class PaginationResponse<T>(
    @SerializedName("items") val items: MutableList<T> = mutableListOf(),
    @SerializedName("total_count") val total: Long
)