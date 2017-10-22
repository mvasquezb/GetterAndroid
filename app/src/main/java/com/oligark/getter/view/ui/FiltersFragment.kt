package com.oligark.getter.view.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oligark.getter.R
import com.oligark.getter.databinding.FragmentFiltersBinding

/**
 * Created by pmvb on 17-10-17.
 */
class FiltersFragment : Fragment() {
    private lateinit var binding: FragmentFiltersBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filters, container, false)

        return binding.root
    }
}