# ZarmBot
## Discord Bot
## Currently under development
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
