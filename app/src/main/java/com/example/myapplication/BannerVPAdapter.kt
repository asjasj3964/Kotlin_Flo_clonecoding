package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentlist : ArrayList<Fragment> = ArrayList() // 이 클래스 안에서만 사용할 변수
    // ViewPager2에서 표시할 Fragment들 저장
    override fun getItemCount(): Int = fragmentlist.size //{ // ViewPager2에서 표시할 Fragment의 총 개수 반환
    //    return fragmentlist.size
    //}
    override fun createFragment(position: Int): Fragment = fragmentlist[position] // fragmentlist 안의 아이템들, 즉 fragment들 생성
    // ex. getItemCount 값이 4라면, 0, 1, 2, 3 까지 실행
    fun addFragment(fragment: Fragment) { // fragmentList에 새로운 Fragment 추가
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size-1) // 새로운 값이 리스트에 추가되는 곳
        // VPAdapter에 아이템이 추가되었음을 알림
    }
}