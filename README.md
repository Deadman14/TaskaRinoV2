# TaskaRinoV2

    /*
    PRIOS
        7 - anything to keep me alive (eat, prayer, run away)
        6 - isGeFullyOpebCheck / death
        5 - Task
        4 - GE
        3 - Banking
        2 - Equipping shit, looting
        1 - nodes
     */


                    BUGS
    account for getting waterskins from bank - I think this is done with my bank for multiuse items
    don't sell all blue wizard hats - in process of adding new way of getting sellables
    during slaying - if player is being attacked by something that is not what it is supposed to be killing, it still kills it


                    IMPROVEMENTS
    Improve sleeps in ernie the chick lever puzzle
    Create method to check if you need a varrock tele to get to GE/slayer master: use this in GE and get slayer task nodes instead
        of bringing varrock teles with you on every task
    Make bot get brass key for edge dungeon
    FInd a way that if bot cannot afford current equipment, use next set down
    Split Utility classes into utility and helper classes.
        -utils stores data or variables used globally
        -helpers contains common code extraced from nodes


                MOVE TO P2P REQS
    40's in all combat styles
    40 fishing
    rune mysteries


                SKILL FOCUSED IN P2P
    Slayer, FIshing, Hunting, Smithing, Mining

                IDEAS
    Make gold items - mine gold, run to furnace, make bars, make item, bank, repeat
    come up with a way to add every item i want to sell to a list and not have to avoid putting in items like my equipment or food I'm using.
        I just wanna put all items in and call a method of like how many of that item should you leave in the bank if any
    replace cooking with smithing?


                Up NEXT
    add in new equipment for higher levels (melee done)
    set konar - 60 att/def 50 slayer
    keep doing new task if avilable
    fishing
    runecrafting miniquest
    runecrafting
    smithing
    prayer
    find a way to tell if your account has mems days on it
        will need for if below x days re-bond


              LOGS
    double dash logs for global "-- comment --"
    single dash logs for node based logs "- comment -"

              VARBITS/CONFIGS
    BIT - 14700: Warning for GE buy offer - off: 1: on 0
    BIT - 14701: Warning for GE sell offer - off: 1: on 0