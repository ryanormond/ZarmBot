# ZarmBot
### Discord Bot
Simple Bot used for counting server emotes. Was built to help with removing or adding of emotes, finds most and least used for better discord server interaction. Has an added function of a poll the server owner can publish for voting. Was being hosted on an AWS server but could be hosted on somthing as small as a Raspberry Pi.

Limitations of SQLite are understood.
Since this is only being used on a few servers (3 atm) SQLite works with queues and timouts on requests as there is not a huge influx of requests all the time on one bot instance. Being a file based database only one connection can write to the file (make changes) at
any one time, requests to make changes to the database have a time out attached to them to provide a buffer should
any requests happen at the same time (this usually doesnt affect the execution time of the requests due to low volume). 

Server owners can run individual instances of the bot currently to avoid any conflict of requests or blocking at all, this may be an avenue to look into for the current capability of the bot. The bot was being run and tested on an AWS EC2 instance and incurrs tiny and even negligible costs PCM as requests to the server are of such low volume, the bot has also been run on a simple apache + Raspberry Pi setup with very good efficiency.

Much more discord servers to support (10+) on a single instance could cause requests to timeout and not be effective, thus it would be recommended to run your own instance of the bot.

## Possible improvements and adaptations
- Implement ROOM for database handling
- re-write in Kotlin

##Current List of Commands
        >>> Commands
        All commands begin with a capital 'Z'

         ***Zhelp*** : You just typed that, so you know it shows you this menu.

         ***Zinit*** : Initializes everything i need to work properly
                        "only use this once as this also resets the emote counter!
                        *Its important to run this when the bot joins the server*

          ***Ztest*** : Simple test to check that im still working!

          ***Zping*** : Simple, you say ping i say pong! the game never ends.

        >>> Commands to do with emotes

         ***Zmue*** : Most used emote, I will tell you what the most used emote is and
                        how many times it has been used.

         ***Zlue*** : Least used emote,I will tell you the least used emote and
                        how many times it has been used.

         ***Zrec*** : Reset emote counter, This command can be used to reset
                        the counter for how much the emotes have been used on the server.
                        This can be useful if you have max custom emotes on the server and
                        cant decide which emote to replace." +

         ***Zreset :emote: value*** : Reset a single emote to a set value
                        The structuere must adhere to this example: Zreset :wave: 20
                        This sets the emote :wave: times used to 20

        ***Zallemotes*** : Shows every custom emote on the server and how many times they have been used!

        ***Ztop5*** : Displays the top 5 most used emotes

        ***Zbot5*** : Displays the bottom 5 least used emotes

        >>> Commands to do with Polls/Voting

        ***Zpoll*** : Creates a new StrawPoll for voting purposes,
                        this was added with the idea that users could vote on which new emotes to be added
                        or which should be replaced.
                        Format must be --> Zpoll Title : option1 option2...
                        There can be between 2 and 30 options in a poll.
