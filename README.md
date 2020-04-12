# ece651-spr20-g1

## master

![pipeline](https://gitlab.oit.duke.edu/cw402/ece651-spr20-g1/badges/master/pipeline.svg)

![coverage](https://gitlab.oit.duke.edu/cw402/ece651-spr20-g1/badges/master/coverage.svg?job=test)

## Coverage
[Detailed coverage](https://cw402.pages.oit.duke.edu/ece651-spr20-g1/dashboard.html)

## UML

[link](https://drive.google.com/file/d/1MILliFXiKYeaP-MawAnwRJEiIybS7V-U/view?usp=sharing)

[evo.2](https://app.creately.com/diagram/YXyNF32eHrc/edit)

## test files
In the *test_files* directory, there are several txt files which can represent a whole process of one game.
* test case 1 --- player2 win
    * player1.txt --- input of player1
    * player2.txt --- input of player2
    
To run this test files, you need to open three terminals(one for server, two for different players).

* terminal 1 --- `gradle run-server`(set up the game server)
* terminal 2 --- `gradle run-client --console=plain < test_files/player1_2.txt`(run player1, will create a new roomInfo)
* terminal 3 --- `gradle run-client --console=plain < test_files/player2_2.txt`(run player2, will join in the roomInfo)

**NOTE**: please strictly follow the order of commands above, otherwise you can't run the test case successfully.

some common errors you will get(if don't follow the order):

* java.net.ConnectException: Connection refused --- you run the client program without setting up the server program
* java.util.NoSuchElementException: No line found --- you run player 2 first(since it will try to join the roomInfo, but the roomInfo is not created) 

