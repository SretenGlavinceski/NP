package NP_LAB8.ex1;



import java.util.ArrayList;
import java.util.List;


public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);

        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());

    }
}
class Song{
    String title;
    String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", artist=" + artist + '}';
    }

}

class MP3Player {
    List<Song> songList;
    int currentSong;
    State state;

    public MP3Player(List<Song> songList) {
        this.songList = songList;
        currentSong = 0;
        state = new PauseState(this);
    }

    public void pressPlay(){
        state.pressPlay();
    }

    public void pressStop(){
        state.pressStop();
    }

    public void pressFWD(){
        System.out.println("Forward...");
        nextSong();
        state = new PauseState(this);
    }

    public void pressREW(){
        System.out.println("Reward...");
        prevSong();
        state = new PauseState(this);
    }

    void printCurrentSong(){
        System.out.println(songList.get(currentSong));
    }

    public int getCurrentSong() {
        return currentSong;
    }

    public void nextSong() {
        currentSong = (currentSong + 1) % songList.size();
    }

    public void prevSong() {
        currentSong = (currentSong + songList.size() - 1) % songList.size();
    }
    public void startOverPlayList() {
        currentSong = 0;
    }

    @Override
    public String toString() {
        return "MP3Player{currentSong = " + currentSong + ", songList = " + songList + "}";
    }


}

interface State{
    void pressPlay();
    void pressStop();
}


abstract class AbstractState implements State{
    MP3Player mp3;

    public AbstractState(MP3Player mp3) {
        this.mp3 = mp3;
    }

}

class PlayState extends AbstractState{
    public PlayState(MP3Player mp3) {
        super(mp3);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop() {
        System.out.println("Song " + mp3.getCurrentSong() + " is paused");
        mp3.state = new PauseState(mp3);
    }
}

class StopState extends AbstractState {
    public StopState(MP3Player mp3) {
        super(mp3);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + mp3.getCurrentSong() + " is playing");
        mp3.state = new PlayState(mp3);
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
    }
}

class PauseState extends AbstractState{
    public PauseState(MP3Player mp3) {
        super(mp3);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + mp3.getCurrentSong() + " is playing");
        mp3.state = new PlayState(mp3);
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        mp3.startOverPlayList();
        mp3.state = new StopState(mp3);
    }
}
