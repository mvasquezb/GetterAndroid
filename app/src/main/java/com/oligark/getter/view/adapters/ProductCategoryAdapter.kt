package com.oligark.getter.view.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.oligark.getter.R
import com.oligark.getter.service.model.ProductCategory
import com.squareup.picasso.Picasso

/**
 * Created by pmvb on 17-11-01.
 */
class ProductCategoryAdapter(
        private var categories: List<ProductCategory>,
        private var categorySelectCallback: ProductCategoryAdapter.CategorySelectCallback
) : RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {

    companion object {
        val TAG = ProductCategoryAdapter::class.java.simpleName
    }

    interface CategorySelectCallback {
        fun onCategorySelected(category: ProductCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent?.context)
                        .inflate(R.layout.filter_category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun setCategories(categoryList: List<ProductCategory>) {
        if (categories.isEmpty()) {
            categories = categoryList
            notifyItemRangeInserted(0, categoryList.size)
            return
        }
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
            ): Boolean = categories[oldItemPosition].id == categoryList[newItemPosition].id

            override fun getOldListSize(): Int = categories.size

            override fun getNewListSize(): Int = categoryList.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldOffer = categories[oldItemPosition]
                val newOffer = categoryList[newItemPosition]
                return oldOffer.hasSameContent(newOffer)
            }
        })
        categories = categoryList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checked = itemView.findViewById<ImageView>(R.id.category_item_check)
        val categoryName = itemView.findViewById<TextView>(R.id.category_item_name)
        val categoryImage = itemView.findViewById<ImageView>(R.id.category_item_img)

        fun bind(category: ProductCategory) {
            Picasso.with(itemView.context).load(category.imageUrl).into(categoryImage)
            categoryName.text = category.name
            itemView.setOnClickListener {
                when (checked.visibility) {
                    View.GONE -> checked.visibility = View.VISIBLE
                    View.VISIBLE -> checked.visibility = View.GONE
                }
                categorySelectCallback.onCategorySelected(category)
            }
        }
    }
}