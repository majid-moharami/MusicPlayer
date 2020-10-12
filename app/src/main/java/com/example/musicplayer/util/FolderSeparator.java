package com.example.musicplayer.util;

import com.example.musicplayer.model.Folder;
import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FolderSeparator {
    public static void main(String[] args) {
        String s = "/storage/emulated/0/Music/Aaron-U-Turn-(Lili)-128.mp3";
        int last = s.lastIndexOf("/");
        String s1 = s.substring(0 , last);
        int last1 = s1.lastIndexOf("/");
        System.out.println(s1.substring( last1+1));
    }

    public static List<Folder> getFolders(List<Song> songs) {
        List<Folder> folders = new ArrayList<>();

        for (int i = 0; i < songs.size(); i++) {
            boolean isExist = false;
            String folder  = getFolderName(songs.get(i).getPath());

            check:
            for (int j = 0; j < folders.size(); j++) {
                 if (folders.get(j).getFolderName().equals(folder)) {
                     isExist = true;
                     break check;
                 }
            }

            if (!isExist){
                List<String> tempFolder = new ArrayList<>();
                for (int j = 0; j < songs.size() ; j++) {
                    String tempFolderName = getFolderName(songs.get(j).getPath());
                    if (tempFolderName.equals(folder)){
                        tempFolder.add(songs.get(j).getPath());
                    }
                }
                folders.add(new Folder(folder , tempFolder));
            }
        }
        return folders;
    }

    private static String getFolderName(String path){
        int last = path.lastIndexOf("/");
        String s1 = path.substring(0 , last);
        int last1 = s1.lastIndexOf("/");
        return s1.substring( last1+1);
    }
}
