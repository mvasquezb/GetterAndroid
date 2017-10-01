package com.oligark.getter.view.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.oligark.getter.R
import com.oligark.getter.service.model.Offer
import java.text.SimpleDateFormat

/**
 * Created by pmvb on 17-10-01.
 */
class OfferAdapter(
        private var offers: List<Offer>,
        private var offerSelectCallback: OfferAdapter.OnOfferSelectCallback
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    companion object {
        val TAG = OfferAdapter::class.java.simpleName
    }

    interface OnOfferSelectCallback {
        fun onOfferSelected(offer: Offer)
    }

    private val simpleFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent?.context)
                        .inflate(R.layout.store_offer_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(offers[position])
    }

    override fun getItemCount(): Int = offers.size

    fun setOfferList(offerList: List<Offer>) {
        if (offers.isEmpty()) {
            offers = offerList
            notifyItemRangeInserted(0, offerList.size)
            return
        }
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
            ): Boolean = offers[oldItemPosition].id == offerList[newItemPosition].id

            override fun getOldListSize(): Int = offers.size

            override fun getNewListSize(): Int = offerList.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldOffer = offers[oldItemPosition]
                val newOffer = offerList[newItemPosition]
                return oldOffer.hasSameContent(newOffer)
            }
        })
        offers = offerList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val offerDescription: TextView = itemView.findViewById(R.id.offer_description)
        val offerDate: TextView = itemView.findViewById(R.id.offer_date)
        val offerGetButton: ImageView = itemView.findViewById(R.id.offer_get_button)

        fun bind(offer: Offer) {
//            Picasso.with(itemView.context)
//                    .load(offer.productImageUrl)
//                    .into(productImage)
            offerDescription.text = offer.description
            offerDate.text = itemView.context.getString(
                    R.string.offer_date,
                    simpleFormat.format(offer.endDate)
            )
            offerGetButton.setOnClickListener {
                offerSelectCallback.onOfferSelected(offer)
            }
        }
    }
}