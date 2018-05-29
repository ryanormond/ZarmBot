
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class ZarmPoll {

    private static final String API_URL = "https://www.strawpoll.me/api/v2/polls";
    private static final String SITE_URL = "https://www.strawpoll.me/";
    private int id = 0;                              //id of the poll
    private String title = "ZarmBot Poll:";           //title of the poll (default place holder)
    private String options = "";                    //options for the poll
    private Channel channel;                        //channel from where the poll request was posted
    private JSONObject Jobj;                         //json object returned by the request
    private boolean multi = false;                  //allow multiple votes, default false

    /**
     * Formats the message into appropriate strings
     * Posts the strawpoll URL to teh channel from where eth request was made
     * @param objMsg
     * @param objChannel
     * @throws IOException
     */
    public void strawpoll(Message objMsg, Channel objChannel) throws IOException {
        this.channel = objChannel;
        String[] str = objMsg.toString().split(":");  //split string into title and options
        this.title = str[1].replace("Zpoll", "");//title into string var
        String[] ops = str[2].split(" ");              //split options into individual
        if(ops.length >= 4){
            for (int i = 1; i <= ops.length -2; i++) {            // -2 because of msg ID and avoid null pointer
                if (i < ops.length -2){
                    this.options = options + "\"" + ops[i] + "\""  + ",";
                } else {
                    this.options = options + "\"" + ops[i] + "\"";
                }
            }
            createPoll();
            this.id = Jobj.getInt("id");
            objChannel.sendMessage(SITE_URL + id);
            options = "";
        }   else{
            objChannel.sendMessage("Either you don't have enough options or you don't have a title\n" +
                    "please use this format --> Title: option1 option2...");
        }
    }

    /**
     * Creates the Connection to API
     * @param request
     * @return
     * @throws IOException
     */
    private HttpsURLConnection createConnection(String request) throws IOException {
        URL url= new URL(API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(request);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "curl/7.5a.0");
        connection.setDoInput(true);
        if(request.equals("POST")){
            connection.setDoOutput(true);
        }
        return connection;
    }

    /**
     *      Request Structure
     *   Destination url: https://strawpoll.me/api/v2/polls
     *  {
     *     "title": "This is a test poll.",
     *     "options": [
     *        "Option #1",
     *        "Option #2"
     *     ],
     *     "multi": true
     *  }
     *  Creates the request to make a new poll
     * @throws IOException
     */
    public void createPoll() throws IOException {
        HttpsURLConnection con = createConnection("POST");   //create connection with request of POST
        DataOutputStream out = new DataOutputStream(con.getOutputStream());//stream to write data to
        //structure the request
        out.writeBytes(
                "{" +
                "\"title\":" + "\"" + title + "\"" + "," +
                "\"options\":" + "[" +
                    options +
                "]," +
                "\"multi\":" + multi +
                "}"
        );
        out.close();//close stream

        //handle returned json
        InputStream in = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while((line = reader.readLine() ) != null) {
            Jobj = new JSONObject(line);
        }
        con.disconnect();
        in.close(); //close input
    }
}//end of class
