import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    //field variables
    private static DiscordAPI api;
    private String token = "";
    private Connection conn = null;

    public static void main(String[] args ) {

        //Initializes the bot
        new App();
        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI discordAPI) {
                MyListener myListener = new MyListener(api);
                api.registerListener(myListener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Constructor
     */
    private App(){
        //api set and read in token for bot
        String file = "token.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            token = reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        api = Javacord.getApi(token, true);
        conn = connect();
    }

    public Connection connect(){
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:database/serversEmotes.db");
                System.out.println("Connection closed: " + conn.isClosed());
                System.out.println("\nConnection to database successful\n");
                //maybe create log files for things like this
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return conn;
    }
}