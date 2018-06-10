package dev.rokoblak.flowrspot.home

import android.arch.paging.PagedListAdapter
import android.view.ViewGroup
import dev.rokoblak.flowrspot.data.Flower

typealias OnFlowerClickedListener = (Flower) -> Unit

typealias OnFlowerFavoritedListener = (flower: Flower, position: Int) -> Unit

class FlowersPagedListAdapter(private val listener: OnFlowerClickedListener, private val favoritedListener: OnFlowerFavoritedListener) :
        PagedListAdapter<Flower, FlowerItem.FlowerViewHolder>(FlowerItem.FlowerDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FlowerItem.createViewHolder(parent)

    override fun onBindViewHolder(holder: FlowerItem.FlowerViewHolder, position: Int) {
        getItem(position)?.let { flower ->
            holder.bind(flower, position, favoritedListener)

            holder.itemView.setOnClickListener {
                listener(flower)
            }
        }
    }
}