package com.winnerwinter.pagergallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*


class GalleryAdapter(val galleryViewModel: GalleryViewModel): ListAdapter<PhotoItem, MyViewHolder>(DIFFCALLBACK) {

    var footerViewStatus = DataStatus.CAN_LOAD_MORE.ordinal

    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

        override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            )
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                    putInt("PHOTO_POSITION", holder.adapterPosition)
                    holder.itemView.findNavController()
                        .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                }
            }
        } else {
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.gallery_footer,
                    parent,
                    false
                ).also { it ->
                    (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                    it.setOnClickListener { itemView ->
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.textView.text = "正在加载"
                        galleryViewModel.fetchData()
                    }
                }
            )
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == itemCount - 1) {
            with(holder.itemView) {
                when (footerViewStatus) {
                    DataStatus.CAN_LOAD_MORE.ordinal -> {
                        progressBar.visibility = View.VISIBLE
                        textView.text = "正在加载"
                        isClickable = false
                    }
                    DataStatus.NO_MORE.ordinal -> {
                        progressBar.visibility = View.GONE
                        textView.text = "全部加载完毕"
                        isClickable = false
                    }
                    DataStatus.NETWORK_ERROR.ordinal -> {
                        progressBar.visibility = View.GONE
                        textView.text = "网络故障，点击重试"
                        isClickable = true
                    }
                }
            }
            return
        }
        val photoItem: PhotoItem = getItem(position)
        with(holder.itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            imageView.layoutParams.height = getItem(position).photoHeight
            textViewUser.text = photoItem.photoUser
            textViewLike.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()
        }
        Glide.with(holder.itemView)
                .load(getItem(position).previewURL)
                .placeholder(R.drawable.photo_placeholder)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false.also { holder.itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                    }
                })
                .into(holder.itemView.imageView)
    }

    object DIFFCALLBACK: DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
