package com.example.myapplication

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {
    lateinit var binding : ActivitySongBinding // 금방 선언
    /*
    * var: 처음에 선언 및 초기화해주고 나중에 그 값에 변경이 가능함
    * ex. var test1 : String = 'dd'
    * val: 처음에 선언을 했으면 나중에 그 값을 변경하지 못함
    * ex. val test2 : Int = 1
    */
    lateinit var song : Song
    lateinit var timer : Timer
    // 추후에 미디어 플레이어를 해제하기 위해 nullable로 선언
    private var mediaPlayer : MediaPlayer? = null
    private var gson : Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSong()
        var title : String? = null
        var singer : String? = null
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text=intent.getStringExtra("title")
            binding.songSingerNameTv.text=intent.getStringExtra("singer")
        }
        binding.songDownIb.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("message", title + " _ " + singer)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
            startStopService()
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
            startStopService()
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("message", "뒤로가기 버튼 클릭")
        setResult(RESULT_OK, intent)
        finish()
    }
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = (song.playTime * binding.songProgressSb.progress) / 100000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songToJson = gson.toJson(song)
        editor.putString("songData", songToJson)
        Log.d("songData", songToJson.toString())
        editor.apply()
    }
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
    }
    fun setPlayerStatus (isPlaying : Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying
        if(isPlaying){ // 재생 중
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else { // 일시정지
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            // 재생 중이 아닐 때, pause를 호출하면 에러가 발생함. 이를 방지하기 위한 조건문
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }
    private fun initSong() {
        if(intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }
        startTimer()
    }
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }
    private fun setPlayer(song : Song) {
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        setPlayerStatus(song.isPlaying)
    }
    private fun startStopService() {
        if (isServiceRunning(ForegroundService::class.java)) {
            Toast.makeText(this, "일시 정지", Toast.LENGTH_SHORT).show()
            stopService(Intent(this, ForegroundService::class.java))
        }
        else {
            Toast.makeText(this, "재생", Toast.LENGTH_SHORT).show()
            startService(Intent(this, ForegroundService::class.java))
        }
    }
    private fun isServiceRunning(inputClass : Class<ForegroundService>) : Boolean {
        val manager : ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (service : ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (inputClass.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }
    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = 0
        private var mills: Float = 0F
        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) {
                        break
                    }
                    while (!isPlaying) {
                        sleep(200) // 0.2초 대기
                    }
                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            // binding.songProgressSb.progress = ((mills/playTime*1000) * 100).toInt()
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }
                        if (mills % 1000 == 0F) { // 1초
                            runOnUiThread {
                                binding.songStartTimeTv.text =
                                    String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("SongActivity", "Thread Terminates! ${e.message}")
            }
        }
    }
}
/*
* hasExtra: intent에 title 또는 singer라는 이름의 데이터가 있는지 확인 -> true/false 반환
* getStringExtra: MainActivity의 재생 바를 클릭했을 때 title과 singer라는 이름으로 전달받은 값 반환
* finish(): 액티비티 종료
* setPlayerStatus()
* : 일시정지 아이콘 클릭 -> 플레이어 상태를 false로 설정 -> 재생 아이콘 나타남
* : 재생 아이콘 클릭 -> 플레이어 상태를 true로 설정 -> 일시정지 아이콘 나타남
*/