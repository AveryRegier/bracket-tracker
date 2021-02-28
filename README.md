[![Build Status](https://travis-ci.org/AveryRegier/bracket-tracker.svg?branch=master)](https://travis-ci.org/AveryRegier/bracket-tracker)
# bracket-tracker
## Tournament Pool and Bracket Tracker

A Java Servlet based generic game tournament pool bracket tracking application suitable especially for the NCAA basketball championship tournament. Features include bracket entering, multiple scoring systems, multiple users, groups, pools, etc.

Tournament Pool and Bracket Tracker contains everything you need to run a tournament pool. It supports small groups or organizations who wish to provide an NCAA basketball tournament pool without a lot of administrative work in registering users or in calculating up-to-date results and possibilities. Though it was built with the NCAA "March Madness" tournaments in mind, it should be able to handle other sport tournament pools that use straight forward brackets.

### How your bracket challenge will proceed
As an administrator, you will first log into the system like any other user. As the first user to log in, you will be granted special site admin privileges. You will have an 'Admin' link in the upper left corner. From there you will be be able to create a new tournament and assign teams to the seeds for the tournament. Included in the distribution are tournament types for the standard NCAA 64 team format, 11 and 12 team formats used by various conferences, the NIT tournament format, and the World Cup Stage 2 format.

Any user can create a group and aministrate their own pool. A group is a set of players that play together in any number of pools. Pool adminstrators can choose from many different scoring systems. Scoring systems can be set up dynamically in the database. Pool administrators will also need to decide if they want to use a tie breaker. If so, they need to set up a tie breaker question. This question will be answered by each player as they enter their bracket into the pool.

When you set up a group, you have a choice. Either you can tell the users about the pool, and have them register, then notify you. You would then add the users to the group manually. This keeps membership totally within your control. However, you can also create an invitation code. You'll then have an link to the system with that invitation code embedded. You can send this link to your users. When they click the link and login (with a possible detour through registration), they will be automatically added to your group. They can then create their bracket and add it to a pool without your direct involvement. You do risk that the email gets circulated to others and people you don't intend to enter your group do. Players can create as many brackets as they like, name them, leave them unfinished, come back to them later, etc. Players can add a bracket to a pool once the bracket is finished either from the bracket interface page or from the pool interface page.


Once a tournament is underway, users can keep track of their progress as the administrator for a tournament updates the results of games. Tournaments results are updated by the administrator with the same interface players use to enter their brackets. Players are prevented from editing their brackets once it has been added to a pool that has a tournament underway. Tournament administrators can delegate the duty of keeping game results up to date to others if they aren't going to be around for some of the games via a simple interface. This allows players to come into the system regularly during the tournament and check their results and the standings much quicker than your typical spreadsheet based systems. If something is updated wrong, the players will keep you honest. The results show current, remaining, and maximum available points, along with a rank and the tie breaker answer if it is used in the pool. When the tournament is completed, the pool administrator may answer the tie breaker question, after which tie breaker answers take effect against the rank.


Any user can inspect the other player's brackets in the pool. The brackets are presented with good picks in the green and bad picks in the red.


### Current Limitations
Right now, all of the bracket, tournament, and teams setup is done via SQL scripts. I typically am working on the scripts the night they are announced. The priority list for the user interface work so far has been first to make the player experience nice, second to make the in-tournament administration nice, and only third to make the tournament setup nice. Hopefully by the time March 2006 rolls around this third area will be improved.

Update: This situation is now officially improved with version 0.4.0. You now have the ability to set up a tournament completely through the user interface. This must be done by a site administrator, through the 'Admin' page.

At this point the bracket system won't handle 'highest seed meets lowest seed' type of brackets. Unfortunately, this rules out using this system for the NFL Playoffs for the time being. I've been working on this, but it is hard nut to crack. The 2006 playoffs showed the worst case scenario for this: a number 6 seed winning it all. If anyone has ideas on how to score brackets for the NFL playoffs, I'd love to hear them.

### Technology
I'm a Java guy, and I don't have places to install PHP based systems. This problem of tracking the brackets was a fun one to tackle, and I wanted to do it in Java. The PHP based systems that are prevalent aren't really hackable for me as I don't know that language well. I think this type of thing is perfect for the web. I like the idea of the excitement that can be generated for the activity if results are immediate and people can inspect them without waiting a day for the admin to get the time to update spreadsheets. All of this conspired together to make me want to write a J2EE based system.

See Getting Started for installation instructions.

### Future Directions
Before you ask, betting isn't handled in this system, and it never will be. I toyed with the idea of just naming it 'Bragging Rights'.

I'd really like to find a feed for game scores. It would be nice if this system could be a player's primary view into the tournament. This would be nice for and internal company pool where the company wants to foster comraderie but not encourage employees to be hitting the sports sites on the net all the time. It would also allow for more real time tracking without having to have a person involved in keeping scores up to date. Unfortately, it seems that sports feeds are big business and quite expensive.

Once the usability issues are all tackled, I'd like to use the system to make it easier for players to make informed decisions while putting in their brackets. I'd like to get win loss records, logos, school colors, etc available. (Lot's of secretaries have done very well based on school colors.)

Another area to tackle is statistical analyses of results, and the brackets people pick.

### Credits
* Avery Regier - Maintainer
* Bill Phillips - User interface updates
* Many thanks to the WebWorks and C&IS teams for their play-testing, ideas, encouragement, and abuse.
