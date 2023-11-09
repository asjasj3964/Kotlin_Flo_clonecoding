package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
//import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.Timer
import java.util.TimerTask
import androidx.recyclerview.widget.LinearLayoutManager

class HomeFragment : Fragment(), CommunicationInterface {
    lateinit var binding : FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private val timer = Timer() // 일정한 간격으로 작업 수행, 주기적으로 슬라이드 변경
    private val handler = Handler(Looper.getMainLooper()) // UI 스레드와 다른 스레드 간의 통신
    // 메인 스레드에 대한 looper를 가져와 슬라이드 변경 작업을 UI 스레드에서 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun sendData(album: Album) {
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        /*
        * Fragment Result API 이용해 데이터 전달
        * setFragmentResult
        * : 첫번째 파라미터 -> 전달받을 Fragment(여기선 AlbumFragment)의 어떤 listener에게 데이터를 전달할 지
        * : 두번째 파라미터 -> 전달한 Bundle 객체
        * bundleOf: 키-값 쌍을 사용해 Bundle 객체를 간단히 생성하는 메서드 ("key to value" 형식)
        */
        /*
        binding.homeAlbumImgIv1.setOnClickListener {
            //val bundle = Bundle()
            //bundle.putString("title", binding.titleLilac.text.toString())
            //bundle.putString("singer", binding.singerIu.text.toString())
            //val albumFragment = AlbumFragment()
            //albumFragment.arguments = bundle
            setFragmentResult("TitleInfo", bundleOf("title" to binding.titleLilac.text.toString()))
            setFragmentResult("SingerInfo", bundleOf("singer" to binding.singerIu.text.toString()))
            (context as MainActivity)
                .supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
        }
        */
        // 데이터 리스트 생성 더미 데이터
       albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        albumRVAdapter.setItemClickListener(object : AlbumRVAdapter.OnItemClickListener {
            override fun onItemClick(album : Album) {
                changeAlbumFragment(album)
            }
            override fun onPlayAlbum(album: Album) {
                sendData(album)
            }
        })
        binding.homePannelBtnMemoIv.setOnClickListener {
            val intent = Intent(requireActivity(), MemoActivity::class.java)
            val activity = requireActivity() // fragment에서 SharedPreferences에 접근하려면 context가 필요함
            val sharedPreferences = activity.getSharedPreferences("memo", AppCompatActivity.MODE_PRIVATE)
            val tempMemo = sharedPreferences.getString("tempMemo", null)
            if(tempMemo != null) {
                val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog, null)
                val builder = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setTitle("메모 복원하기")
                val alertDialog = builder.show()
                val yesBtn = alertDialog.findViewById<Button>(R.id.yes)
                val noBtn = alertDialog.findViewById<Button>(R.id.no)
                yesBtn!!.setOnClickListener {
                    startActivity(intent)
                }
                noBtn!!.setOnClickListener {
                    val editor = sharedPreferences.edit()
                    editor.remove("tempMemo")
                    editor.apply()
                    startActivity(intent)
                }
            } else {
                startActivity(intent)
            }
        }
        /*
        * Bundle 이용해 데이터 전달
        * 별도의 의존성 없이 사용할 수 있는 방법, 자주 사용됨
        * Bundle 객체 생성 후, Bundle에 key-value 쌍의 데이터를 넣음 (여기선 가수의 이름과 노래 제목)
        * AlbumFragment의 인스턴스인 albumFragment 생성 후 arguments 속성을 방금 만든 bundle로 set
        * replace 메서드를 사용해 albumFragment로 전환
        */
        // ViewPager와 VPAdapter 연결
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 좌우로 스크롤
        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)
        autoSlide(bannerAdapter)
        // ViewPager와 VPAdapter 연결
        val pannelAdpater = PannelVpAdapter(this)
        pannelAdpater.addFragment(PannelFragment(R.drawable.img_first_album_default))
        pannelAdpater.addFragment(PannelFragment(R.drawable.img_first_album_default))
        binding.homePannelBackgroundVp.adapter = pannelAdpater
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)
        return binding.root
        /*
        * context -> 현재 Fragment의 액티비티(MainActivity)에 엑세스
        * supportFragmentManager -> main_frm을 AlbumFragment로 교체
        * Fragment의 onCreateView 메서드에서 binding을 inflate로 바인딩 객체를 초기화(binding = FragmentHomeBinding.inflate)
        * binding.root 반환해 해당 레이아웃을 화면에 표시
        */
    }
    private fun autoSlide(adapter: BannerVPAdapter) {
        timer.scheduleAtFixedRate(object : TimerTask() { // 주기적으로 실행할 작업 지정
            override fun run() {
                handler.post { // UI 스레드에서 코드 실행
                    val nextItem = binding.homeBannerVp.currentItem + 1
                    if (nextItem < adapter.itemCount) {
                        binding.homeBannerVp.currentItem = nextItem
                    } else {
                        binding.homeBannerVp.currentItem = 0 // 순환
                    } // 현재 보여지는 뷰페이저의 아이템 인덱스 가져옴, 다음 아이템이 뷰페이저의 아이템 수를 초과하면 첫 번째 아이템으로 돌아가도록 설정
                }
            } // 추상 클래스인 TimerTask의 run 메서드를 오버라이드 -> 주기적으로 실행할 코드 정의
        }, 3000, 3000) // 최초 실행 딜레이 시간 3초, 주기 3초
    }
    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumToJson = gson.toJson(album)
                    putString("album", albumToJson)
                }
            })
            .commitAllowingStateLoss()
    }
}