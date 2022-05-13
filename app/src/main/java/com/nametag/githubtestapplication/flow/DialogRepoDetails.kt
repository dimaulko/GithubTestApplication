package com.nametag.githubtestapplication.flow

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nametag.githubtestapplication.R
import com.nametag.githubtestapplication.data.network.Contributor
import com.nametag.githubtestapplication.data.network.getRepoName
import com.nametag.githubtestapplication.databinding.DialogRepoDetailsBinding
import com.nametag.githubtestapplication.databinding.VhAdapterContributorBinding
import com.nametag.githubtestapplication.databinding.VhAdapterLanguageBinding
import com.nametag.githubtestapplication.di.gone
import com.nametag.githubtestapplication.di.show
import com.nametag.githubtestapplication.di.subscribe
import com.nametag.githubtestapplication.utils.GlideApp
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class DialogRepoDetails : BottomSheetDialogFragment() {

    private var binding: DialogRepoDetailsBinding? = null
    lateinit var mViewModel: MainVM

    private var behavior: BottomSheetBehavior<View>? = null

    companion object {
        val TAG = DialogRepoDetails::class.java.simpleName

        @JvmStatic
        fun newInstance(): DialogRepoDetails {
            return DialogRepoDetails()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogThemeModal)
        mViewModel = getSharedViewModel()
        mViewModel.loadRepoInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogRepoDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe(mViewModel.sharedRepo) {
            binding?.apply {
                tvUserName.text = it.owner.login
                tvRepoName.text = it.getRepoName()
                tvRepoCreationDate.text = it.createdAt.split("T")[0]
                tvRepoLastModificationDate.text = it.updatedAt.split("T")[0]
                GlideApp.with(this.ivUserAvatar)
                    .load(it.owner.avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(this.ivUserAvatar)
            }
        }

        subscribe(mViewModel.dataRepoLanguagesLivaData) {
            binding?.progressBar?.gone()
            if (it.first.isNotEmpty()) {
                binding?.tvTitleLanguage?.show()
                binding?.rvListLanguages?.show()
                binding?.rvListLanguages?.apply {
                    val values = it.first.keys.toList()
                    layoutManager = LinearLayoutManager(
                        this@DialogRepoDetails.requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = AdapterLanguage().apply {
                        submitList(values)
                    }
                    show()
                }
            }
            if (it.second.isNotEmpty()) {
                binding?.tvTitleContributors?.show()
                binding?.rvListContributors?.show()
                binding?.rvListContributors?.apply {
                    layoutManager = LinearLayoutManager(
                        this@DialogRepoDetails.requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = ContributorAdapter().apply {
                        submitList(it.second)
                    }

                }
            }
        }
    }
}

class AdapterLanguage : ListAdapter<String, AdapterLanguage.LanguageVH>(
    object :
        DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageVH {
        return LanguageVH(
            VhAdapterLanguageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LanguageVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LanguageVH(
        var binding: VhAdapterLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: String) {
            binding.tvLanguage.text = value
        }
    }
}

class ContributorAdapter : ListAdapter<Contributor, ContributorAdapter.ContributorVH>(
    object :
        DiffUtil.ItemCallback<Contributor>() {
        override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorVH {
        return ContributorVH(
            VhAdapterContributorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContributorVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContributorVH(
        var binding: VhAdapterContributorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: Contributor) {
            GlideApp.with(binding.ivAvatar)
                .load(value.avatarUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivAvatar)
        }
    }
}
