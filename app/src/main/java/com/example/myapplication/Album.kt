package com.example.myapplication

data class Album(
    var title : String? = "",
    var singer : String? = "",
    var coverImage : Int? = null,
    var songs: ArrayList<Song>? = null // 앨범 수록곡
)
