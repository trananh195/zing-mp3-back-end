package com.example.du_an_tn_zingmp_3.controller;

import com.example.du_an_tn_zingmp_3.model.PlayList;
import com.example.du_an_tn_zingmp_3.service.IPlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/playLists")
public class PlayListController {
    @Autowired
    private IPlayListService iPlayListService;

    @GetMapping
    public ResponseEntity<Iterable<PlayList>> findAllPlayList(){
        return new ResponseEntity<>(iPlayListService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PlayList>> findOnePlayList(@PathVariable("id") Long id){
        return new ResponseEntity<>(iPlayListService.findById(id),HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> createPlayList(@RequestBody PlayList playList){
        if (playList.getNamePlayList().isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        iPlayListService.save(playList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/findByName/{name}")
    public ResponseEntity<Optional<PlayList>> findByNamePlayList(@PathVariable String namePlayList){
        return new ResponseEntity<>(iPlayListService.findByName(namePlayList), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById (@PathVariable("id") Long id){
        iPlayListService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/deleteSongInPlaylist/{idSong}/{idPlaylist}")
    public ResponseEntity<?> deleteSongInPlaylist(@PathVariable("idSong") Long idSong,
                                     @PathVariable("idPlaylist") Long idPlaylist){
        iPlayListService.deleteSongInPlaylist(idSong, idPlaylist);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/findByIdUser/{id}")
    public ResponseEntity<List<PlayList>> findAllByIdUser(@PathVariable("id") Long idUser) {
        return new ResponseEntity<>(iPlayListService.finAllByIdUser(idUser), HttpStatus.OK);
    }
}
