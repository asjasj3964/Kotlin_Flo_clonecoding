package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()
    private val information = arrayListOf("수록곡", "상세정보", "영상") // TabLayout에 사용될 Label을 지정하기 위한 arrayList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater,container,false)
        val albumToJson = arguments?.getString("album")
        val album = gson.fromJson(albumToJson, Album::class.java)
        setInit(album)
        setFragmentResultListener("TitleInfo"){ requestKey, bundle ->
            binding.albumMusicTitleTv.text = bundle.getString("title")
        }
        setFragmentResultListener("SingerInfo"){ requestKey, bundle ->
            binding.albumSingerNameTv.text = bundle.getString("singer")
        }
        /*
        binding.albumMusicTitleTv.text = arguments?.getString("title")
        binding.albumSingerNameTv.text = arguments?.getString("singer")*
        */
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }
        /*
        binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }
        binding.songFluLayout.setOnClickListener {
            Toast.makeText(activity,"FLU", Toast.LENGTH_SHORT).show()
        }
        binding.songCoinLayout.setOnClickListener {
            Toast.makeText(activity,"Coin", Toast.LENGTH_SHORT).show()
        }
        binding.songSpringLayout.setOnClickListener {
            Toast.makeText(activity,"봄 안녕 봄", Toast.LENGTH_SHORT).show()
        }
        binding.songCelebrityLayout.setOnClickListener {
            Toast.makeText(activity,"Celebrity", Toast.LENGTH_SHORT).show()
        }
        binding.songSingLayout.setOnClickListener {
            Toast.makeText(activity,"돌림노래 (Feat. DEAN)", Toast.LENGTH_SHORT).show()
        }
        */
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position -> // TabLayout을 ViewPage2와 연결하는 중재자
            tab.text = information[position] // 현재 position에 해당하는 TabLayout의 Tab 객체
        }.attach() // TabLayout과 ViewPage를 붙여줌
        return binding.root
    }
    private fun setInit(album : Album) {
        binding.albumAlbumIv.setImageResource(album.coverImage!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
    }
}