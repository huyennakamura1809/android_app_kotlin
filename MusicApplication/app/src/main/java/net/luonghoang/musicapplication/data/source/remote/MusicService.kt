package net.braniumacademy.musicapplication.data.source.remote

import net.braniumacademy.musicapplication.data.model.album.AlbumList
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.model.song.SongList
import retrofit2.Response
import retrofit2.http.GET

interface MusicService {
    @GET("/resources/braniumapis/songs.json")
    suspend fun loadSongs(): Response<SongList>

    @GET("/resources/braniumapis/playlist.json")
    suspend fun loadAlbums(): Response<AlbumList>

    // cung cấp api cho ds ca sĩ
    @GET("resources/braniumapis/artists.json")
    suspend fun loadArtists(): Response<ArtistList>
}