package dev.rokoblak.flowrspot.home

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import dev.rokoblak.flowrspot.R
import dev.rokoblak.flowrspot.data.Flower
import org.jetbrains.anko.find

object FlowerItem {

    private fun inflate(parent: ViewGroup) = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_flower_home, parent, false)

    fun createViewHolder(parent: ViewGroup) = FlowerViewHolder(inflate(parent))

    class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView by lazy { itemView.find<ImageView>(R.id.flower_image) }
        private val favoriteIcon: ImageView by lazy { itemView.find<ImageView>(R.id.icon_favorite) }
        private val nameText: TextView by lazy { itemView.find<TextView>(R.id.text_name) }
        private val latinNameText: TextView by lazy { itemView.find<TextView>(R.id.text_latin_name) }
        private val sightingsText: TextView by lazy { itemView.find<TextView>(R.id.text_sightings) }

        fun bind(flower: Flower, position: Int, favoritedListener: OnFlowerFavoritedListener?) {
            nameText.text = flower.name
            latinNameText.text = flower.latin_name

            val sightingsFormat = sightingsText.context.getString(R.string.pattern_flower_sightings)
            sightingsText.text = String.format(sightingsFormat, flower.sightings)

            val imageUrl = with(flower.profile_picture) {
                if (startsWith("//")) {
                    "https:$this"
                } else {
                    this
                }
            }
            if (imageUrl.isNotBlank()) {
                Glide.with(image).load(imageUrl).into(image)
            }

            val favIconTintId = if (flower.favorite) R.color.star_favorite_on else R.color.star_favorite_off
            favoriteIcon.setColorFilter(ContextCompat.getColor(favoriteIcon.context, favIconTintId), PorterDuff.Mode.MULTIPLY)

            favoritedListener?.let { listener ->
                favoriteIcon.setOnClickListener {
                    flower.favorite = !flower.favorite
                    listener(flower, position)
                }
            }
        }
    }

    object FlowerDiffUtilCallback : DiffUtil.ItemCallback<Flower>() {
        // Always just one type
        override fun areItemsTheSame(oldItem: Flower?, newItem: Flower?) = true
        // Equality by content
        override fun areContentsTheSame(oldItem: Flower?, newItem: Flower?) = oldItem?.equals(newItem) ?: false
    }
}
