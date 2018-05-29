import de.btobastian.javacord.entities.Channel;

public class Commands {

    /**
    * Simple Ping Pong Command
     */
    public static void ping(Channel objChannel) {
        objChannel.sendMessage("pong");
    }

    /**
     * Commands list
     */
    public static void help(Channel objChannel) {
        objChannel.sendMessage("***** Dont worry i can help! *****" + " \n " +
        "Heres all the things i can do and a brief description." + " \n " +
        " " + " \n " +
        ">>> Commands" + " \n " +
        "All commands begin with a capital 'Z' " +
                        " \n \n " +
        " ***Zhelp*** : You just typed that, so you know it shows you this menu." +
                        " \n \n " +
        " ***Zinit*** : Initializes everything i need to work properly \n" +
                        "only use this once as this also resets the emote counter!" +
        "Its important to run this when the bot joins the server" + " " +
                        "\n \n" +
         " ***Ztest*** : Simple test to check that im still working!" +
                        " \n\n " +
         " ***Zping*** : Simple, you say ping i say pong! the game never ends." +
                        " \n \n " +
        ">>> Commands to do with emotes" +
                        " \n " +
        " ***Zmue*** : Most used emote, I will tell you what the most used emote is and " +
                        "how many times it has been used." +
                        " \n \n " +
        " ***Zlue*** : Least used emote,I will tell you the least used emote and " +
                        "how many times it has been used." +
                        " \n \n " +
        " ***Zrec*** : Reset emote counter, This command can be used to reset " +
                        "the counter for how much the emotes have been used on the server." +
                        "This can be useful if you have max custom emotes on the server and " +
                        "cant decide which emote to replace." +
                        "\n \n " +
        "***Zallemotes*** : Shows every custom emote on the server and how many times they have been used!" +
                        "\n \n " +
        "***Ztop5*** : Displays the top 5 most used emotes" +
                        "\n \n" +
        "***Zbot5*** : Displays the bottom 5 least used emotes" +
                        "\n \n" +
        ">>> Commands to do with Polls/Voting" +
                        "\n" +
        "***Zpoll*** : Creates a new StrawPoll for voting purposes, " +
                        "this was added with the idea that users could vote on which new emotes to be added" +
                        "or which should be replaced.\n\n" +
                        "Format must be --> Zpoll Title : option1 option2...\n" +
                        "There can be between 2 and 30 options in a poll." +
                        "\n \n " +

        "***Any suggestions for further features? private Message > @Zarmortiz < " +
                        "and leave your ideas with him. Thank You!***"
        );
    }
}