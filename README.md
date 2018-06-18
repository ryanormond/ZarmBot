# ZarmBot
### Discord Bot
### Currently under development
Current scaling of bot capability underway.
Previously used for research into some possibilities of the bots functions, now being developed into a full fledged distributable bot.
Adding some small scaling capability to support more servers, SQLite is being added for hard storage and better access
to data used by the bot.

Limitations of SQLite are understood and further implementations will address this.
Since this is only being used on a couple of servers (max 3 atm) SQLite works nicely as a full server is not required for it and all can
be done locally alongside the bot when running. Being a file based database only one connection can write to the file (make changes) at
any one time, requests to make changes to the database currently have a time out attached to them to provide some allowance should
any requests happen at the same time (this usually doesnt affect the execution time of the requests due to low volume of requests).

Much more discord servers to support (possibly 10-15+) would require a full MYSQL database as access would be needed somtimes
simultaniously and data integrity needs to be upheld. This is for future implementation if more development is done.

## Future Implementations
- Fullscale MYSQL database
- Better hosting
- Apache/XAMPP

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
