package com.oligark.getter.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.oligark.getter.R
import com.oligark.getter.service.model.Offer
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

/**
 * Created by pmvb on 17-10-01.
 */
class OfferAdapter(
        private var offers: List<Offer>,
        private var offerSelectCallback: OfferAdapter.OnOfferSelectCallback
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    interface OnOfferSelectCallback {
        fun onOfferSelected(offer: Offer)
    }

    private val simpleFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm")

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val offerDescription: TextView = itemView.findViewById(R.id.offer_description)
        val offerDate: TextView = itemView.findViewById(R.id.offer_date)
        val offerGetButton: ImageButton = itemView.findViewById(R.id.offer_get_button)

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