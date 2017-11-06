package com.oligark.getter.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.oligark.getter.R
import com.oligark.getter.databinding.FragmentFiltersBinding
import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.view.adapters.ProductCategoryAdapter
import com.oligark.getter.view.adapters.ProductCategoryWrapper
import com.oligark.getter.viewmodel.FiltersViewModel
import com.oligark.getter.viewmodel.resources.DataResource

/**
 * Created by pmvb on 17-10-17.
 */
class FiltersFragment : Fragment(), ProductCategoryAdapter.CategorySelectCallback {
    companion object {
        val TAG = FiltersFragment::class.java.simpleName
    }

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
        setHasOptionsMenu(true)

        filtersViewModel = ViewModelProviders.of(activity).get(FiltersViewModel::class.java)

        setupCategoryItems()
        setupPriceRange()
        binding.filterApplyBtn.setOnClickListener {
            filtersViewModel.applyFilters()
            activity.onBackPressed()
        }

        return binding.root
    }

    private fun setupPriceRange() {
        val maxRange = filtersViewModel.priceMaxValue - filtersViewModel.priceMinValue
        binding.priceRangeBar.setSteps(maxRange.toFloat() / 100)
        binding.priceRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            binding.priceMinValue.text = "$minValue"
            binding.priceMaxValue.text = "$maxValue"
        }
        binding.priceRangeBar.setOnRangeSeekbarFinalValueListener({ minValue, maxValue ->
            filtersViewModel.updatePriceRange(minValue.toInt(), maxValue.toInt())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_filters, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.btn_menu_reset_filters -> {
                filtersViewModel.clearAll()
                setCategories(filtersViewModel.productCategories.value?.items ?: listOf())
                resetPriceRange()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetPriceRange() {
        binding.priceRangeBar.setMinValue(filtersViewModel.priceMinValue.toFloat())
        binding.priceRangeBar.setMaxValue(filtersViewModel.priceMaxValue.toFloat())
        binding.priceRangeBar.invalidate()
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
                    setCategories(categories.items)
                }
                DataResource.LoadState.ERROR -> {
                    hideLoading()
                    showError()
                }
                else -> {}
            }
        })
    }

    private fun setCategories(items: List<ProductCategory>) {
        categoryAdapter.setCategories(
                items.map {
                    val wrapper = ProductCategoryWrapper(it)
                    wrapper.selected = filtersViewModel.selectedProductCategories.contains(it.id)
                    wrapper
                }
        )
    }

    override fun onCategorySelected(category: ProductCategory) {
        filtersViewModel.selectCategory(category)
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
}