Simple card game
================

This is a simple single player card game. 
The human player plays against 3 AI opponents.
The goal is to get the score of 30 points or more first.

## Rules

The game is played with a standard 52 card deck.
Every round one card is drawn from the deck and the players bid on it.
The player with the highest bid pays the coins and gets the points equal to the value of the card.
At the end of the round the first player is changed.
At the beginning of every round (starting from the second round) the players get 1 coin.
The game ends when one of the players reaches 30 points or more.

All players start with 0 points and 10 coins.

### Bidding
Each round the players bid coins starting with the first player.
Every bid must be higher than the previous one.
The player with the highest bid gets the number of points equal to the value of the card.

### Passing
The player can skip the bid if he doesn't want to bid (or can't bid higher).
This is called "passing".

## Future features

* Settings screen (language, background music)
* Optimize for Windows
* Compile for mobile platforms
* Animations (screen transitions, UI element transformations)
* Very hard difficulty level (deep learning AI)
* Local multiplayer (hot seat)
* Global multiplayer 
  * Shared leaderboard
* New game modes (blind bidding, special powers)

## Design

### Technologies
Project is created with:
* Kotlin Compose for Desktop
* Kotlin Coroutines

### Structure
PlantUML class diagram of the main components:
```plantuml
@startuml

Player --o Game
Game -l-> GameState
Game --> PlayerAction

Deck -o GameState
Card -u-* Deck

GameState *-- Round

Card --o Round
PlayerAction --o Round

@enduml
```

### Flow diagram
PlantUML flow diagram of the game flow:
```plantuml
@startuml
start
:start game;
repeat :draw card;
#pink:update state;
note left: updated card
while (have all players played this round?) is (no)
    :progress to next player;
    #pink:update state; 
    note left: updated current player
    if (is current player human?) then (yes)
        :wait for input;
    else (no)
        :generate bet;
    endif
    :place bet;
    #pink:update state;
    note left: updated bet
end while
->yes;
:determine round winner;
:progress to next round;
#pink:update state;
note left 
    updated 
    * scores and coins
    * first player
    * current player
end note
if (any player has 30 points or more?) then (yes)
    :determine overall winner;
    #pink:update state;
    note left: updated winner
    :game over;
    end
else (no)
    :next round;
    #pink:update state;
    note left: updated round
endif
 
@enduml
```

# How to build and run

Build a fat JAR with Gradle
```
./gradlew shadowJar
```

and run it with Java
```
java -jar build/libs/simple-card-game-[version]-all.jar
```
