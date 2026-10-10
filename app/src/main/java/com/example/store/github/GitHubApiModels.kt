package com.example.store.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUserResponse(
    @Json(name = "login") val login: String,
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "html_url") val htmlUrl: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "public_repos") val publicRepos: Int? = 0,
    @Json(name = "followers") val followers: Int? = 0
)

@JsonClass(generateAdapter = true)
data class GitHubRepoResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "stargazers_count") val stargazersCount: Int? = 0,
    @Json(name = "forks_count") val forksCount: Int? = 0,
    @Json(name = "language") val language: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)
