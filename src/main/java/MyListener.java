import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MyListener implements MessageCreateListener {

    private final DiscordAPI api;
    private final ZarmPoll zPoll;
    private Message objMsg;
    private Channel objChannel;
    private User objUser;
    private Commands commands;
    private EmoteCounter eCounter;
    private static HashMap<String, Runnable> hmap;

    public MyListener(DiscordAPI api) {
        //API if needed
        this.api = api;
        //class objects
        commands = new Commands();
        eCounter = new EmoteCounter(api);
        zPoll = new ZarmPoll();
        //Hash map which stores commands, key = name, runnable = method related to command
        this.hmap = new HashMap<String, Runnable>() {
            {
                //Commands Class
                put("Zping", () -> Commands.ping(objChannel)); //simple ping pong test
                put("Zhelp", () -> Commands.help(objChannel)); //simple ping pong test
                //EmoteCounter Class
                put("Zinit", () -> eCounter.BotInitializer(objMsg, objChannel)); //Initializes anything the bot needs
                put("Zmue", () -> eCounter.mostUsedEmote(objChannel)); //most used commands
                put("Zlue", () -> eCounter.leastUsedEmote(objChannel)); //least used commands
                put("Zrec", () -> eCounter.resetEmoteCounter(objMsg, objChannel)); //reset command counter
                put("Zallemotes", () -> {//displays every emote in order of times used
                    try {
                        eCounter.allEmotes(objChannel);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        objChannel.sendMessage("Somethings not right! I didn't execute this correctly *confused*");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        objChannel.sendMessage("Sorry i got Interrupted, could u do it again");
                    }
                });

                put("Ztop5", () -> {
                    try {
                        eCounter.top5emotes(objChannel);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        objChannel.sendMessage("Somethings not right! I didn't execute this correctly *confused*");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        objChannel.sendMessage("Sorry i got Interrupted, could u do it again");
                    }
                });
                //displays bottom 5 used emotes
                put("Zbot5", () -> eCounter.bottom5emotes(objChannel));
                //Strawpoll Class
                // calls to create/retrieve a strawpoll
                put("Zpoll", () -> {
                    try {
                        zPoll.strawpoll(objMsg,objChannel);
                    } catch (IOException e) {
                        e.printStackTrace();
                        objChannel.sendMessage("Somethings not right, Lets Try again *confused*");
                    }
                });
            }
        };
    }

    @Override
    public void onMessageCreate(DiscordAPI discordAPI, Message message) {
        //message info extractions
        objMsg = message;
        objChannel = message.getChannelReceiver();
        objUser = message.getAuthor();

        //Responds to any user who says "Ztest"
        if (objMsg.getContent().equals("Ztest")) {
            objChannel.sendMessage("hello : " + objUser.getMentionTag() +"! i am working");
        }
        //check for command
        if(objMsg.getContent().startsWith("Z")) {
                String[] arr = objMsg.getContent().split(" ");
            if(hmap.containsKey(arr[0])){
                hmap.get(arr[0]).run();
            }
        }
        //checks for emote/emojis used
        if(message.getContent().contains(":") && !message.getContent().contains("Zpoll")){
            if (!message.getAuthor().isBot()) {
                eCounter.messageWithEmote(message);
            }

        }
    }
}
