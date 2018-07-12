
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class EmoteCounter{

    private final DiscordAPI api;
    private static Connection conn;
    private Collection<CustomEmoji> emojis;

    /**
     *  Constructor
     * @param api connection to discord api
     */
    public EmoteCounter(DiscordAPI api) {
        this.api = api;
        this.conn = this.connect();
    }


    /**
     * Creates connection to the database
     * @return returns connection to SQLite database
     */
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
     * @param objMsg message from discord
     * @param objChannel channel the message was posted in
     */
    public void BotInitializer(Message objMsg, Channel objChannel) {
        objChannel.sendMessage("Initialized : setup");
        ResetEmoteList(objMsg);
    }

    /**
     * Inserts a NEW entry into the database
     * Replaces existing row if already present
     * @param id server id
     * @param emote emote name
     * @param timesUsed how many times used
     */
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

    /**
     * Removes a selected row from database
     * Used to remove emotes individually
     * @param id server id
     * @param emotes name of emote
     */
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

    /**
     * Adds the server if it doesnt exist to the database
     * @param id servers id
     * @param owner owner of the discord servers id
     */
    private void insertNewServer(String id, String owner){
        String sql = "INSERT INTO servers (serverid, owner)" +
                    " VALUES ('" + id + "','" + owner + "');";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeServer(String id, String owner){

    }

    /**
     * Updates a row in the database
     * @param id server id
     * @param emotes emote name
     */
    private void update(String id, String emotes){
        String sql = "UPDATE emotes " +
                "SET timesUsed = timesUsed + 1 " +
                "WHERE serverid = '" + id + "' AND emote = '" + emotes + "'";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes current emote list to empty and repopulates
     * with all fresh emotes.
     * @param message message from discord
     */
    public void ResetEmoteList(Message message){
        String serverID = message.getChannelReceiver().getServer().getId();
        emojis = api.getServerById(serverID).getCustomEmojis();
        EmoteCounter app = new EmoteCounter(api);
        for (CustomEmoji e: emojis) {
            app.insert(serverID, e.getName(), 0);
        }
    }

    /**
     * Resets a single emote
     * @param message message from discord
     * @param objChannel channel the message was posted in
     */
    public void resetSingleEmote(Message message, Channel objChannel){
        if (message.getAuthor().getId().equals(objChannel.getServer().getOwnerId())){
            String id = message.getChannelReceiver().getServer().getId();
            String[] str = message.toString().split("[:()<>\\s]");
            try{
                String emote = str[5];
                int num = Integer.parseInt(str[8]);
                String sql = "SELECT timesUsed FROM emotes " +
                        "WHERE serverid = '" + id + "' AND emote = '" + emote + "'";
                try {
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setQueryTimeout(10);
                    ResultSet rs = pstmt.executeQuery();
                    int previous = rs.getInt("timesUsed");
                    if (num < previous){
                        insert(id, emote, num - 1);
                        objChannel.sendMessage(emote + " has been reset succesfully");
                    } else {
                        objChannel.sendMessage( "New Value must be less than old value");
                    }
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch(ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException ex){
                objChannel.sendMessage( "Wrong use of command\n" +
                                            "Format should be e.g. **Zreset :emote: 50** " +
                                            "(sets the times the emote was used to 50)");
            }
        } else {
            objChannel.sendMessage("You don't have permission to do that");
        }
    }

    /**
     * Handles message with emote/s
     * @param message message from discord
     */
    public void messageWithEmote(Message message) {
        String id = message.getChannelReceiver().getServer().getId();
        String sql = "SELECT serverid FROM servers " +
                "WHERE serverid = '" + id + "'";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet rs = pstmt.executeQuery();
            String compareid = "";
            if (rs.next()) {
                compareid = rs.getString("serverid");
            }
            if (!id.equals(compareid)) {
                ResetEmoteList(message);
                insertNewServer(id, message.getChannelReceiver().getServer().getOwnerId());
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String word : message.getContent().split(":")) {
            update(id, word);
        }
    }

    /**
     * Selects the most used emote of the server where request was made
     * @param objChannel channel the message was posted in
     */
    public void mostUsedEmote(Channel objChannel) {
        String id = objChannel.getServer().getId();
        String sql = "SELECT emote, timesUsed FROM emotes" +
                " WHERE serverid = " + id +
                " ORDER BY timesUsed DESC LIMIT 1";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet result = pstmt.executeQuery();

            String emote = result.getString("emote");
            int times = result.getInt("timesUsed");
            objChannel.sendMessage("Oh my!" + " \n " +
                    "\\:" + emote + "\\: " + "has been used " + times + " times!");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            objChannel.sendMessage("Im sorry somthing went wrong");
        }
    }

    /**
     * Shows the least used emote on the server
     * @param objChannel channel the message was posted in
     */
    public void leastUsedEmote(Channel objChannel) {
        String id = objChannel.getServer().getId();
        String sql = "SELECT emote, timesUsed FROM emotes" +
                " WHERE serverid = " + id +
                " ORDER BY timesUsed ASC LIMIT 1";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet result = pstmt.executeQuery();

            String emote = result.getString("emote");
            int times = result.getInt("timesUsed");
            if(times == 0){
                objChannel.sendMessage( ":" + emote + ": has never been used!");
            } else {
                objChannel.sendMessage("People dont use " +
                        "\\:" + emote + "\\: " + "very much, only " + times + " times!");
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            objChannel.sendMessage("Im sorry somthing went wrong");
        }
    }

    /**
     * Sorts the emotes by value order and sends ot the channel.
     * Somehow this works.
     * if complications occur with thread.sleep() .get() can be added to the end of .sendMessage("")
     * @param objChannel channel the message was posted in
     */
    public void allEmotes(Channel objChannel) {
        String id = objChannel.getServer().getId();
        String sql = "SELECT emote, timesUsed FROM emotes" +
                " WHERE serverid = " + id +
                " ORDER BY timesUsed DESC";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet result = pstmt.executeQuery();
            String allEmotes = "";
            while(result.next()){
                String emote = result.getString("emote");
                String timesUsed = result.getString("timesUsed");
                allEmotes = allEmotes + "\\:" + emote + "\\:" + " > " + timesUsed + ",   ";
            }
            objChannel.sendMessage(allEmotes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the top 5 emotes on the server
     * thread sleep is used as order can sometimes be out of sync.
     * @param objChannel channel the message was posted in
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void top5emotes(Channel objChannel){
        String id = objChannel.getServer().getId();
        String sql = "SELECT emote, timesUsed FROM emotes" +
                " WHERE serverid = " + id +
                " ORDER BY timesUsed DESC LIMIT 5";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet result = pstmt.executeQuery();
            String allEmotes = "";
            int num = 1;
            while(result.next()){
                String emote = result.getString("emote");
                String timesUsed = result.getString("timesUsed");
                allEmotes = allEmotes + "#" + num + " " + emote + " > " + timesUsed + ",   ";
                num++;
            }
            objChannel.sendMessage(allEmotes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the bottom 5 emotes on the server
     * thread sleep is used as order can sometimes be out of sync.
     * @param objChannel channel the message was posted in
     */
    public void bottom5emotes(Channel objChannel){
        String id = objChannel.getServer().getId();
        String sql = "SELECT emote, timesUsed FROM emotes" +
                " WHERE serverid = " + id +
                " ORDER BY timesUsed ASC LIMIT 5";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setQueryTimeout(10);
            ResultSet result = pstmt.executeQuery();
            String allEmotes = "";
            int num = 1;
            while(result.next()){
                String emote = result.getString("emote");
                String timesUsed = result.getString("timesUsed");
                allEmotes = allEmotes + "#" + num + " " + emote + " > " + timesUsed + ",   ";
                num++;
            }
            objChannel.sendMessage(allEmotes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the counter for emotes
     * @param message message from discord
     * @param objChannel channel the message was posted in
     */
    public void resetEmoteCounter(Message message, Channel objChannel) {
        String serverID = objChannel.getServer().getId();
        if(message.getAuthor().getId().equals(api.getServerById(serverID).getOwnerId()) ){
            ResetEmoteList(message);
            objChannel.sendMessage("Emotes are reset" + message.getAuthor().getMentionTag());
        } else {
            objChannel.sendMessage(message.getAuthor().getMentionTag() +
                    " you don't have permission to do that.");
        }
    }

    /**
     *
     */
    public void addEmote(Message message, Channel objChannel){
        String serverID = objChannel.getServer().getId();
        if(message.getAuthor().getId().equals(api.getServerById(serverID).getOwnerId()) ){
            System.out.println(message.toString());
            if (message.getContent().matches("Zadd\\s:[a-zA-Z]+:")){
                String[] str = message.toString().split(":");
                insert(serverID, str[2], 0);
                objChannel.sendMessage("Added " + str[2] + " to my list" + message.getAuthor().getMentionTag());
            } else {
                objChannel.sendMessage("Please use the format > Zadd :emote:");
            }
        } else {
            objChannel.sendMessage(message.getAuthor().getMentionTag() +
                    " you don't have permission to do that.");
        }
    }

    /**
     *
     */
    public void removeEmote(Message message, Channel objChannel){
        String serverID = objChannel.getServer().getId();
        if(message.getAuthor().getId().equals(api.getServerById(serverID).getOwnerId()) ){
            if (message.getContent().matches("Zremove\\s:[a-zA-Z]+:")){
                String[] str = message.toString().split(":");
                remove(serverID, str[2]);
                objChannel.sendMessage("Removed " + str[2] + message.getAuthor().getMentionTag());
            } else {
                objChannel.sendMessage("Please use the format > Zremove :emote:");
            }
        } else {
            objChannel.sendMessage(message.getAuthor().getMentionTag() +
                    " you don't have permission to do that.");
        }
    }
}
