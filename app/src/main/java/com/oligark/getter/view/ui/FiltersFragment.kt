package com.oligark.getter.view.ui

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

/**
 * Created by pmvb on 17-10-17.
 */
class FiltersFragment : Fragment(), ProductCategoryAdapter.CategorySelectCallback {
    private lateinit var binding: FragmentFiltersBinding
    private lateinit var categoryAdapter: ProductCategoryAdapter

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
        setupCategoryItems()
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
    }

    override fun onCategorySelected(category: ProductCategory) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(activity, category.slug, Toast.LENGTH_SHORT).show()
    }
}