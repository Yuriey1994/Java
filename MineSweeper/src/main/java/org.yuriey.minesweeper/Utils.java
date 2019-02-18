package org.yuriey.minesweeper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.yuriey.minesweeper.model.Rank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static ObservableList<Rank> getRank() {
        try {
            File file = new File("rank");
            if (!file.exists()){
                file.createNewFile();
                new ObjectOutputStream(new FileOutputStream(file)).writeObject(new ArrayList<Rank>());
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<Rank> list = ((List) ois.readObject());
            ois.close();
            return FXCollections.observableArrayList(list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }
    public static void writeRank(ObservableList<Rank> observableList) {
        try {
            File file = new File("rank");
            if (!file.exists()){
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(new ArrayList<Rank>(observableList));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
