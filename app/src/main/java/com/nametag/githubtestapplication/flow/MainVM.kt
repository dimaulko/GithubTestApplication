package com.nametag.githubtestapplication.flow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nametag.githubtestapplication.data.network.Contributor
import com.nametag.githubtestapplication.data.network.PaginationResponse
import com.nametag.githubtestapplication.data.network.Repo
import com.nametag.githubtestapplication.data.providers.IGithubProvider
import com.nametag.githubtestapplication.utils.NetworkException
import com.nametag.githubtestapplication.utils.RxUtils
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

class MainVM(
    private val iGithubProvider: IGithubProvider
) : ViewModel() {

    companion object {
        val ITEMS_PER_PAGE = 100
    }

    var sharedRepo = MutableLiveData<Repo>()
    var loadRepoInfoDisposable: Disposable? = null
    val dataRepoLanguagesLivaData = MutableLiveData<Pair<Map<String, Long>, List<Contributor>>>()


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var searchStringNFTStringAccount: String = ""
    var sorting: Sorting = Sorting.STARS

    var loadedAllPage = false
    var maxElements = Int.MAX_VALUE
    var loadReposListDisposable: Disposable? = null
    val dataListLivaData = MutableLiveData<List<Repo>>()
        .apply { value = emptyList() }

    var loadingState: MutableLiveData<PaginationState> = MutableLiveData<PaginationState>().apply {
        value = PaginationState.VALIDATION_ERROR
    }

    fun setNewSorting(value: Sorting) {
        sorting = value
        startLoadSearch(searchStringNFTStringAccount)
    }

    fun prepareDataForNewLoad(repo: Repo) {
        sharedRepo.value = repo
        dataRepoLanguagesLivaData.value = emptyMap<String, Long>() to emptyList()
    }

    fun startLoadSearch(value: String) {
        loadedAllPage = false
        loadingState.value = PaginationState.IDLE
        searchStringNFTStringAccount = value
        dataListLivaData.value = emptyList()
        loadReposListDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        loadNextPage()
    }

    fun startLoadMain() {
        loadedAllPage = false
        loadingState.value = PaginationState.IDLE
        searchStringNFTStringAccount = ""
        dataListLivaData.value = emptyList()
        loadReposListDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        loadNextPage()
    }

    fun loadNextPage(refresh: Boolean = false) {
        if (loadingState.value == PaginationState.LOADING) {
            return
        }
        if (loadedAllPage) {
            return
        }
        loadingState.value = PaginationState.LOADING
        val currentList = if (refresh) emptyList() else dataListLivaData.value!!

        val page = currentList.size / ITEMS_PER_PAGE
        val request: Single<PaginationResponse<Repo>> = iGithubProvider.searchRepo(
            searchStringNFTStringAccount,
            sorting.sortingName,
            ITEMS_PER_PAGE,
            page
        )

        loadReposListDisposable = request
            .compose(RxUtils.ioToMainTransformerSingle())
            .subscribe(
                {
                    if (it!!.items.isEmpty()) {
                        loadingState.value = PaginationState.NO_ITEMS
                    } else {
                        dataListLivaData.postValue(currentList + it.items)
                        loadingState.value = PaginationState.IDLE
                    }

                    maxElements = it.total.toInt()
                    loadedAllPage =
                        dataListLivaData.value!!.size == it.total.toInt()
                },
                {
                    if (it is NetworkException.ValidationException) {
                        loadingState.value = PaginationState.VALIDATION_ERROR
                    } else {
                        loadingState.value = PaginationState.ERROR
                    }
                    Timber.d(it)
                }
            )
        compositeDisposable.add(loadReposListDisposable!!)
    }


    //-----

    fun loadRepoInfo() {

        loadRepoInfoDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        val singleLanguage = iGithubProvider.getLanguagesForRepos(
            sharedRepo.value!!.owner.login,
            sharedRepo.value!!.repoFullName.split("/")[0]
        )
            .onErrorResumeNext { error -> Single.just(emptyMap()) }

        val singleContributor = iGithubProvider.getContributorsForRepos(
            sharedRepo.value!!.owner.login,
            sharedRepo.value!!.repoFullName.split("/")[0]
        )
            .onErrorResumeNext { error -> Single.just(emptyList()) }

        loadRepoInfoDisposable = singleLanguage.zipWith(singleContributor) { lan, con ->
            Pair(lan, con)
        }
            .compose(RxUtils.ioToMainTransformerSingle())
            .subscribe({
                dataRepoLanguagesLivaData.value = it
            }, {
                dataRepoLanguagesLivaData.value = emptyMap<String, Long>() to emptyList()
            })


    }

}

enum class Sorting(val sortingName: String) {
    STARS("stars"),
    FORKS("forks"),
    UPDATED("updated")
}

sealed class PaginationState {
    object IDLE : PaginationState()
    object LOADING : PaginationState()
    object NO_ITEMS : PaginationState()
    object ERROR : PaginationState()
    object VALIDATION_ERROR : PaginationState()
}