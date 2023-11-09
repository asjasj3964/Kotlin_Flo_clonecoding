package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.widget.TextView
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var song : Song = Song()
    private var gson : Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_UMCFlo)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        binding.mainPlayerCl.setOnClickListener {
            startActivity(Intent(this, SongActivity::class.java))
        }
        */
        initBottomNavigation()
        /*
        val song = Song(binding.mainMiniplayerTitleTv.text.toString(),
            binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false, "music_lilac")
        */
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val message = data.getStringExtra("message")
                    Log.d("message", message!!)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        /*
        Log.d("Song", song.title + song.singer)
        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
        }
        */
        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this,SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer",song.singer)
            //startActivity(intent)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
            activityResultLauncher.launch(intent)
        }
    }
    private fun initBottomNavigation(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()
        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) { // 아이디를 눌렀을 때 각각의 프래그먼트로 전환할 수 있도록 함
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                /*
                * supportFragmentManager.beginTransaction()
                * : 현재 엑티비티의 FragmentManager를 가져와 새로운 FragmentTransaction 시작
                * FragmentManager: Fragment의 추가, 교체 및 관리 담당
                * replace(): main_frm FrameLayout 컨테이너에 HomeFragment 추가 또는 교체
                * commitAllowingStateLoss(): FragmentTransaction을 커밋하는 메서드
                * ==> MainActivity 실행 시 HomeFragment(default)가 화면에 표시됨
                * setOnItemSelectedListener
                * : BottomNavigationView의 항목을 클릭할 때 발생하는 이벤트 처리
                * when(item.itemId)
                * : 클릭된 BottomNavigationView의 항목의 ID에 따라 다른 동작을 수행하기 위해 사용
                * return@setOnItemSelectedListener true
                * : 항목 선택 이벤트 처리 후 true 반환 -> Fragment 변경 완료
                * : false 반환 -> 항목 선택 이벤트가 소비되지 않도록 하여 다른 코드나 리스너에서 추가적으로 처리할 수 있도록 만듦
                * (여기선 단순히 when의 문법을 지키기 위해 사용)
                */
                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val jsonToSong = sharedPreferences.getString("songData", null)
        Log.d("jsonToSong", jsonToSong.toString())
        song = if(jsonToSong == null) { // 최초 실행 시
            Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
        } else { // SongActivity에서 노래가 한번이라도 pause된 경우
            gson.fromJson(jsonToSong, Song::class.java)
        }
        setMiniPlayer(song)
    }
    private fun setMiniPlayer(song : Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second * 100000 / song.playTime)
    }
    fun updateMainPlayerCl(album : Album) {
        binding.mainMiniplayerTitleTv.text = album.title
        binding.mainMiniplayerSingerTv.text = album.singer
        binding.mainMiniplayerProgressSb.progress = 0
    }
}