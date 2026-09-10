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
