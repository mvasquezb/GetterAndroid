package com.oligark.getter.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.oligark.getter.R
import com.oligark.getter.databinding.FragmentFiltersBinding
import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.view.adapters.ProductCategoryAdapter
import com.oligark.getter.viewmodel.FiltersViewModel
import com.oligark.getter.viewmodel.resources.DataResource

/**
 * Created by pmvb on 17-10-17.
 */
class FiltersFragment : Fragment(), ProductCategoryAdapter.CategorySelectCallback {
    private lateinit var binding: FragmentFiltersBinding
    private lateinit var categoryAdapter: ProductCategoryAdapter

    private lateinit var filtersViewModel: FiltersViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filters, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.appbar.toolbar)
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.appbar.toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }
        filtersViewModel = ViewModelProviders.of(activity).get(FiltersViewModel::class.java)

        setupCategoryItems()

        filtersViewModel.init()
        return binding.root
    }

    private fun setupCategoryItems() {
        binding.categoryItems.layoutManager = GridLayoutManager(
                activity,
                3,
                GridLayoutManager.VERTICAL,
                false
        )
        categoryAdapter = ProductCategoryAdapter(listOf(), this)
        binding.categoryItems.adapter = categoryAdapter
        filtersViewModel.productCategories.observe(activity, Observer { categories ->
            when (categories?.loadState) {
                DataResource.LoadState.LOADING -> {
                    hideError()
                    showLoading()
                }
                DataResource.LoadState.COMPLETED -> {}
                DataResource.LoadState.SUCCESS -> {
                    hideLoading()
                    categoryAdapter.setCategories(categories.items)
                }
                DataResource.LoadState.ERROR -> {
                    hideLoading()
                    showError()
                }
                else -> {}
            }
        })
    }

    private fun hideError() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showError() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun hideLoading() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showLoading() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCategorySelected(category: ProductCategory) {
        filtersViewModel.selectCategory(category)
    }
}