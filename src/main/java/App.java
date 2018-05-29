import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;

public class App {
    //field variables
    private static DiscordAPI api;

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
        api = Javacord.getApi("MzU3MTUwMTQ4MDA4MDgzNDcw.DJltkQ.17BKcW8CL7Or3bsEtKS0ivIUDHQ", true);
    }
}