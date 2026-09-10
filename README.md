Java BE Bot that plays Dragons of Mugloar game.

The bot needs Java 21 or newer to run.

## Running
Build the jar:
```sh
./mvnw package
```
Run it:
```sh
java -jar target/mugloar-bot.jar
```

That plays one game against the live API at `https://dragonsofmugloar.com/api/v2`, prints one
line per turn, and prints the final score when the lives run out.

## Strategy

What the bot does with each turn, in order:

- If it has 1 life left and can afford a healing potion, it buys one.
- Otherwise it looks at every ad on the board and gives each one a value: ad's reward is multiplied by
  the probability score
- If the best ad is worth less than 30 gold, it buys the cheapest non-potion item instead, as
  long as 100 gold is left over for a potion afterwards. Every item except the potion adds a level,
  and a higher level makes future ads easier.
- Otherwise it solves the best ad. Ties go to the ad that expires soonest.
- If the board is empty and there is nothing to buy, the run stops.

## Potential updates / TODO

- Turns and levels should take each other into consideration. Sometimes the bot levels too much and it doesn't increase the odds.
It could do something else instead. For example, stack on health potions
- Bot doesn't use "investigation" endpoint at all. Not sure what it even does. Probably affects the quest outcomes but the 
relation is hidden. It might be possible to take some words from quest's descriptions into consideration but not sure.
- Picking an ad should be the default case. I think "investigation" (because it doesn't cost gold, nor it loses lives)
should be the default case. Bot shouldn't solve impossible ad-s, instead it should wait for solvable ones.
- run bots in parallel.
- add retries to POST/GET endpoints
- For funs, let LLM make decisions instead by replacing the decisions method with a call/promt to an LLM.
