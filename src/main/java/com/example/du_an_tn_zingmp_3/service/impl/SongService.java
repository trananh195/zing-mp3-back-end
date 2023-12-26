package com.example.du_an_tn_zingmp_3.service.impl;

import com.example.du_an_tn_zingmp_3.model.PlayList;
import com.example.du_an_tn_zingmp_3.model.Songs;
import com.example.du_an_tn_zingmp_3.repository.ISongRepository;
import com.example.du_an_tn_zingmp_3.service.IPlayListService;
import com.example.du_an_tn_zingmp_3.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongService implements ISongService {
   @Autowired
    private ISongRepository iSongRepository;
   @Autowired
   private IPlayListService iPlayListService;


    @Override
    public Iterable<Songs> findAll() {
        return iSongRepository.findAll();
    }

    @Override
    public Optional<Songs> findById(Long id) {
        return iSongRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        iSongRepository.deleteById(id);
    }

    @Override
    public void save(Songs songs) {
        iSongRepository.save(songs);
    }

    @Override
    public Optional<Songs> findByNameSong(String name) {
        return iSongRepository.findByNameSong(name);
    }

    @Override
    public Iterable<Songs> findAllByNameSong(String name) {
        return iSongRepository.findAllByNameSong(name);
    }

    @Override
    public void addPlayList(Long idPlayList, Long idSong) {
        Songs songs = findById(idSong).get();
        PlayList playList = iPlayListService.findById(idPlayList).get();
        playList.getSongsList().add(songs);
        iPlayListService.save(playList);
    }

    @Override
    public Iterable<Songs> searchAllByName(String name) {
        return iSongRepository.searchByNameSong(name);
    }

    @Override
    public List<Songs> searchAllByIdUser(Long idUser) {
        Iterable<Songs> songsIterable = findAll();
        List<Songs> songs = new ArrayList<>();
        for (Songs songs1: songsIterable) {
            if (songs1.getUser() != null) {
                if (songs1.getUser().getId().equals(idUser)) {
                    songs.add(songs1);
                }
            }
        }
        return songs;
    }

    @Override
    public List<Songs> findAllByIdPlayList(Long idPlaylist) {
        Iterable<PlayList> playLists = iPlayListService.findAll();
        List<Songs> songs = new ArrayList<>();
        for (PlayList playlist: playLists) {
            if (Objects.equals(playlist.getId(), idPlaylist)){
                songs.addAll(playlist.getSongsList());
                break;
            }
        }
        return songs;
    }

    @Override
    public List<Songs> findAllByNameSinger(String nameSinger) {
        return iSongRepository.findAllBySinger(nameSinger);
    }

    @Override
    public List<Songs> findAllByIdSongType(Long id) {
        Iterable<Songs> songsList = findAll();
        List<Songs> list = new ArrayList<>();
        for (Songs songs : songsList) {
            if (songs.getId_SongTypes().getId() == id){
                list.add(songs);
            }
        }
        return list;
    }
}
