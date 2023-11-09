package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>(){
    // 뷰 홀더를 생성해줘야 할 때 호출되는 함수. 아이템 뷰 객체를 만들어서 뷰홀더에 전달
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }
    // 뷰홀더에 데이터를 바인딩해줘야 할 때마다 호출되는 함수. 자주 호출됨
    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(albumList[position])
        }
        holder.binding.itemAlbumPlayImgIv.setOnClickListener {
            itemClickListener.onItemClick(albumList[position])
        }
    }
    // 데이터 세트 크기를 알려주는 함수. 리사이클러뷰가 마지막이 언제인지 알게 됨
    override fun getItemCount(): Int = albumList.size
    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImage!!)
        }
    }
    interface OnItemClickListener {
        fun onItemClick(album : Album)
        fun onPlayAlbum(album : Album)
    }
    private lateinit var itemClickListener : OnItemClickListener
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}