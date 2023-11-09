package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3 // 수록곡, 상세정보, 영상 -> 각각의 다른 뷰를 가지고 있음
    override fun createFragment(position: Int): Fragment {
        return when(position) { // 조건에 따라 다른 작업 수행
            0 -> SongFragment()
            1 -> DetailFragment()
            else -> VideoFragment()
        }
    }
}