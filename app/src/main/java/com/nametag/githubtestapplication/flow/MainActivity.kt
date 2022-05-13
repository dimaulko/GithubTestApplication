package com.nametag.githubtestapplication.flow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nametag.githubtestapplication.R
import com.nametag.githubtestapplication.data.network.Repo
import com.nametag.githubtestapplication.data.network.getRepoName
import com.nametag.githubtestapplication.databinding.ActivityMainBinding
import com.nametag.githubtestapplication.databinding.VhAdapterFooterBinding
import com.nametag.githubtestapplication.databinding.VhAdapterReposBinding
import com.nametag.githubtestapplication.di.*
import com.nametag.githubtestapplication.utils.GlideApp
import com.nametag.githubtestapplication.utils.RxSearchObservable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mViewModel: MainVM
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var footerAdapter = FooterAdapter(::onRetryFeature)
    private var repoAdapter = RepoAdapter(::onClickOpenRepoDetails, ::openRepoInfo)
    private var concatAdapter = ConcatAdapter(repoAdapter, footerAdapter)

    companion object {
        private const val THRESHOLD = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mViewModel = getViewModel()
        setContentView(binding.root)
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = concatAdapter
        }
            .also {
                it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0
                            && (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + THRESHOLD >= (recyclerView.layoutManager as LinearLayoutManager).itemCount
                        ) {
                            mViewModel.loadNextPage(false)
                        }
                    }
                })
            }
        binding.vSearch.setOnCloseListener {
            mViewModel.startLoadMain()
            true
        }
        if (mViewModel.searchStringNFTStringAccount.isNotEmpty()) {
            binding.vSearch.setQuery(mViewModel.searchStringNFTStringAccount, false)
        }

        when (mViewModel.sorting) {
            Sorting.STARS -> binding.radioStars.isChecked = true
            Sorting.FORKS -> binding.radioForks.isChecked = true
            Sorting.UPDATED -> binding.radioUpdated.isChecked = true
        }

        compositeDisposable.add(
            RxSearchObservable.fromView(binding.vSearch)
                .debounce(150, TimeUnit.MILLISECONDS)
                .filter { text -> text.isNotEmpty() }
                .map { text -> text.lowercase(Locale.getDefault()).trim() }
                .distinctUntilChanged()
                .switchMap { s -> Observable.just(s) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { query ->
                    mViewModel.startLoadSearch(query)
                }
        )

        subscribeOnLiveData()
    }

    fun subscribeOnLiveData() {
        subscribe(mViewModel.dataListLivaData) {
            repoAdapter.submitList(it)
        }
        subscribe(mViewModel.loadingState) {
            when (it) {
                is PaginationState.IDLE -> {
                    footerAdapter.submitList(listOf())
                    binding.tvValidationMessage.gone()
                }
                is PaginationState.VALIDATION_ERROR -> {
                    footerAdapter.submitList(listOf())
                    binding.tvValidationMessage.show()
                }
                else -> {
                    footerAdapter.submitList(listOf(it))
                    binding.tvValidationMessage.gone()
                }
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {

            val checked = view.isChecked

            when (view.getId()) {
                R.id.radio_stars ->
                    if (checked) {
                        mViewModel.setNewSorting(Sorting.STARS)
                    }
                R.id.radio_forks ->
                    if (checked) {
                        mViewModel.setNewSorting(Sorting.FORKS)
                    }
                R.id.radio_updated ->
                    if (checked) {
                        mViewModel.setNewSorting(Sorting.UPDATED)
                    }
            }
        }
    }

    private fun onRetryFeature() = mViewModel.loadNextPage(false)
    private fun onClickOpenRepoDetails(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun openRepoInfo(value: Repo) {
        hideKeyboard(this)
        mViewModel.prepareDataForNewLoad(value)
        val dialog = supportFragmentManager.findFragmentByTag(DialogRepoDetails.TAG)
        if (dialog == null) {
            val fragment = DialogRepoDetails.newInstance()
            fragment.isCancelable = true
            supportFragmentManager.beginTransaction().add(fragment, DialogRepoDetails.TAG)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction().show(dialog).commitAllowingStateLoss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }
}


class FooterAdapter(private val onRetry: () -> Unit) :
    ListAdapter<PaginationState, FooterAdapter.VH>(object :
        DiffUtil.ItemCallback<PaginationState>() {
        override fun areItemsTheSame(oldItem: PaginationState, newItem: PaginationState) = true

        override fun areContentsTheSame(oldItem: PaginationState, newItem: PaginationState) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            VhAdapterFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: VhAdapterFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonRetry.setOnClickListener {
                onRetry()
            }
        }

        fun bind(item: PaginationState) {
            when (item) {
                PaginationState.LOADING -> {
                    binding.run {
                        clRoot.show()
                        buttonRetry.gone()
                        tvError.gone()
                        progressBar.show()
                        tvNoData.gone()
                    }
                }
                is PaginationState.ERROR -> {
                    binding.run {
                        clRoot.show()
                        buttonRetry.show()
                        tvError.show()
                        progressBar.gone()
                        tvNoData.gone()
                    }
                }
                is PaginationState.NO_ITEMS -> {
                    binding.run {
                        clRoot.show()
                        buttonRetry.gone()
                        tvError.gone()
                        progressBar.gone()
                        tvNoData.show()
                    }
                }
                else -> {
                    binding.run {
                        clRoot.gone()
                        buttonRetry.gone()
                        tvError.gone()
                        progressBar.gone()
                        tvNoData.gone()
                    }
                }
            }
        }
    }
}


class RepoAdapter(
    var actionClickProfile: ((String) -> Unit)? = null,
    var actionOpenRepoInfo: ((Repo) -> Unit)? = null
) :
    ListAdapter<Repo, RepoAdapter.VH>(object :
        DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            VhAdapterReposBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            actionClickProfile,
            actionOpenRepoInfo
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(
        private val binding: VhAdapterReposBinding,
        var actionClickProfile: ((String) -> Unit)? = null,
        var actionOpenRepoInfo: ((Repo) -> Unit)? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivUserImage.setOnClickListener {
                data?.let {
                    actionClickProfile?.invoke(it.owner.htmlUrl)
                }
            }
            binding.clRoot.setOnClickListener {
                data?.let {
                    actionOpenRepoInfo?.invoke(it)
                }
            }
        }

        var data: Repo? = null
        fun bind(item: Repo) {
            data = item
            data?.let {
                GlideApp.with(binding.ivUserImage)
                    .load(it.owner.avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .transforms(MultiTransformation(CenterCrop(), RoundedCorners(dpToPx(2))))
                    .into(binding.ivUserImage)
                binding.tvUserName.text = it.owner.login
                binding.tvRepoName.text = it.getRepoName()
                binding.tvWatchers.text = it.watchers.toString()
                binding.tvFork.text = it.forksCount.toString()
                binding.tvIssues.text = it.openIssuesCount.toString()
            }
        }
    }
}
