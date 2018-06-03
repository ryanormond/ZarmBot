
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.CustomEmoji;
import de.btobastian.javacord.entities.message.Message;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class EmoteCounter{

    private final DiscordAPI api;
    private static Connection conn;
    private HashMap<String, Integer> countEmotes;
    private Collection<CustomEmoji> emojis;
    private String serverID;
    private int msgCount;

    public EmoteCounter(DiscordAPI api) {
        this.api = api;
        msgCount = 0;
        this.conn = this.connect();
    }


    private Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database/serversDB.db");
            System.out.println("Connection closed: " + connection.isClosed());
            System.out.println("\nConnection to database successful\n");
            //maybe create log files for things like this
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Initialises everything for a server
     * @param objMsg
     * @param objChannel
     */
    public void BotInitializer(Message objMsg, Channel objChannel) {
        objChannel.sendMessage("Initialized : setup");
        ResetEmoteList(objMsg);
    }

    private void insert(String id, String emote, int timesUsed) {

        String sql = "INSERT INTO emotes (serverid, emote, timesUsed)\n" +
                    "VALUES ('" + id + "','" + emote + "'," + timesUsed + ");";
        try {
            remove(id, emote);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void remove(String id, String emotes){
        String sql = "DELETE FROM emotes " +
                    "WHERE serverid = '" + id + "' AND emote = '" + emotes + "'";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(String id, String emotes){
        String sql = "UPDATE emotes " +
                "SET timesUsed = timesUsed + " + 1 +
                "WHERE serverid = '" + id + "' AND emote = '" + emotes + "'";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes current emote list to empty and repopulates
     * with all fresh emotes.
     * @param message
     */
    public void ResetEmoteList(Message message){
        serverID = message.getChannelReceiver().getServer().getId();
        emojis = api.getServerById(serverID).getCustomEmojis();
        EmoteCounter app = new EmoteCounter(api);
        for (CustomEmoji e: emojis) {
            app.insert(serverID, e.getName(), 0);
        }
    }

    /**
     * resets a single emote
     */
    public void resetSingleEmote(Message message, Channel objChannel){
        String id = message.getChannelReceiver().getServer().getId();
        String[] str = message.toString().split(":");
        String emote = str[2];
        insert(id, emote, 0);
    }

    /**
     * Handles message with emote/s
     * @param message
     */
    public void messageWithEmote(Message message) {
        String id = message.getChannelReceiver().getServer().getId();
        String sql = "SELECT serverid FROM servers " +
                "WHERE serverid = " + id;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    boolean found = rs.getBoolean(1);
                    if (found){
                        for (String word : message.getContent().split(":")) {
                            update(id, word);
                        }
                    } else {
                        ResetEmoteList(message);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the most used emote on the server
     */
    public void mostUsedEmote(Channel objChannel) {
        if (!countEmotes.isEmpty()){
            Collection c = countEmotes.values();
            for (String key: countEmotes.keySet()) {
                if (Collections.max(c) == countEmotes.get(key)) {
                    objChannel.sendMessage("Oh my!" + " \n " +
                            ":" + key + ": " + "has been used " + Collections.max(c) + " times!");
                }
            }
        } else {
            objChannel.sendMessage("Please use command: Zinit  first to setup all emote related commands");
        }
    }

    /**
     * Shows the least used emote on the server
     * @param objChannel
     */
    public void leastUsedEmote(Channel objChannel) {
        if(!countEmotes.isEmpty()){
            Collection c = countEmotes.values();
            for (String key: countEmotes.keySet()) {
                if (Collections.min(c) == countEmotes.get(key)) {
                    objChannel.sendMessage("People dont use this emote much" + " \n " +
                            ":" + key + ": " + "has only been used " + Collections.min(c) + " times!");
                }
            }
        } else {
            objChannel.sendMessage("Please use command: Zinit  first to setup all emote related commands");
        }
    }

    /**
     * Sorts the emotes by value order and sends ot the channel.
     * Somehow this works.
     * if complications occur with thread.sleep() .get() can be added to the end of .sendMessage("")
     * @param objChannel
     */
    public void allEmotes(Channel objChannel) throws ExecutionException, InterruptedException {
        Set<Entry<String, Integer>> set = countEmotes.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort( list, (o1, o2) -> (o2.getValue()).compareTo( o1.getValue() ));
        String emotes = "";
        if(!countEmotes.isEmpty()){
            objChannel.sendMessage("Heres the list: ").get();
            for(Map.Entry<String, Integer> entry:list){
                emotes = (emotes + ":" + entry.getKey() + ": > " + entry.getValue() + ",  ");
            }
            objChannel.sendMessage(emotes);
        } else {
            objChannel.sendMessage("Please use command: Zinit  first to setup all emote related commands");
        }

    }

    /**
     * Shows the top 5 emotes on the server
     * thread sleep is used as order can sometimes be out of sync.
     * @param objChannel
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void top5emotes(Channel objChannel) throws ExecutionException, InterruptedException {
        Set<Entry<String, Integer>> set = countEmotes.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort( list, (o1, o2) -> (o2.getValue()).compareTo( o1.getValue() ));
        if(!countEmotes.isEmpty()){
            for(int i = 0; i < 5; i++){
                objChannel.sendMessage(":" + list.get(i) + ":" );
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    objChannel.sendMessage("hmm i broke something... \n might need to try again");
                }
            }
        } else {
            objChannel.sendMessage("Please use command: Zinit  first to setup all emote related commands");
        }
    }

    /**
     * Shows the bottom 5 emotes on the server
     * thread sleep is used as order can sometimes be out of sync.
     * @param objChannel
     */
    public void bottom5emotes(Channel objChannel){
        Set<Entry<String, Integer>> set = countEmotes.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort( list, (o1, o2) -> (o2.getValue()).compareTo( o1.getValue() ));
        Collections.reverse(list);
        if(!countEmotes.isEmpty()){
            for(int i = 0; i < 5; i++){
                objChannel.sendMessage(i+"." + list.get(i));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    objChannel.sendMessage("hmm i broke something... \n might need to try again");
                }
            }
        } else {
            objChannel.sendMessage("Please use command: Zinit  first to setup all emote related commands");
        }
    }

    /**
     * Resets the counter for emotes
     * @param message
     * @param objChannel
     */
    public void resetEmoteCounter(Message message, Channel objChannel) {
        if(message.getAuthor().getId().equals(api.getServerById(serverID).getOwnerId()) ){
            ResetEmoteList(message);
            objChannel.sendMessage("Emotes are reset" + message.getAuthor().getMentionTag());
        } else {
            objChannel.sendMessage(message.getAuthor().getMentionTag() +
                    " you don't have permission to do that.");
        }
    }
}
