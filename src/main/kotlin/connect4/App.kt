package connect4
import io.javalin.Javalin
import io.javalin.http.Context



var ai="red"
var human="yellow"
//enables and disables the test mod
var testmod=false
//choosing the player 1
var player1:String= "human"

//choosing the difficulty
var difficulty:Int=2

//game scenario
var scenario="none"

//the whole board
val slots = MutableList(42) { Slot() }

//color of the current player
var currentcolor: String = human

//current player identifier
var playerselector: String ="human"


//cell that will be filled on the server side
var celltofill =   Slot()

//the score for the minimax alg
var scores : HashMap<String, Int> = HashMap ()


//history arraylist
var history: MutableList<Int> = ArrayList()
////////////////////////////////////////////



//
var scenario1=false
var scenario2=false
var scenario3=false
var scenario4=false
var scenario5=false



fun main(args : Array<String>) {

//setting the score logic for the minimax alg

    //color of the human player
    scores.put(human , -10000000)
    //color of the ai
    scores.put(ai , 10000000)
    //tie case
    scores.put("tie" , -1)

    var loop: Int = 0
    while (loop < slots.size) {
        slots[loop].color = "white"
        slots[loop].slot = loop
        loop++
    }


    var move :Int
////////////////////////////////////////////



    val app = Javalin.create { config ->
        config.addStaticFiles("/public")
    }.start(4000)





    app.get("/") { ctx: Context ->
        ctx.redirect("choice.html")


    }
    app.get("/settings") { ctx: Context ->

        testmod=false
        player1= ctx.queryParam("player1").toString()
        difficulty=ctx.queryParam("difficulty")!!.toInt()





        celltofill.winner="no one"
        celltofill.victory=false
        currentcolor=human
        for(i in 0..41){
            slots[i].color = "white"
        }

        if(player1=="ai" ){
            playerselector="ai"
            filling(bestMove())
            history.add(celltofill.aimove)
            slots[celltofill.aimove].color = celltofill.aicolor


            playerselector="human"
        }


        ctx.redirect("index.html")
    }
    /******************************Test Cases**************************************************************/
    var movecnt=0
    app.get("/testcase"){ctx: Context ->
        /*  if(testmod==false){
              testmod=true
          }
          else{
              testmod=false
          }
          */
        testmod=true
        scenario=ctx.queryParam("tst").toString()
        movecnt=0
        if(scenario=="Szenario 1"){

            history.clear()
            difficulty=1

            for(loop in 0 ..41){
                slots[loop].color = "white"
            }
            slots[40].color=ai
            slots[35].color= human
            slots[39].color=ai
            slots[36].color= human
            slots[38].color=ai

            movecnt=0
            playerselector="human"
            println("#######################Test Scenario 1 Started:#######################\n*Game Depth is: 1\n*Player 1 is: AI\n*Moves Made :\n-1-The AI filled the column N:7")
            println("-2-The Human filled the column N:1\n-3-The AI filled the column N:4\n-4-The Human filled the column N:2\n-5-The AI filled the column N:6")
        }
        if(scenario=="Szenario 3"){

            history.clear()
            difficulty=5

            for(loop in 0 ..41){
                slots[loop].color = "white"
            }
            slots[38].color=human
            slots[31].color= ai


            movecnt=0
            playerselector="human"
            println("#######################Test Scenario 3 Started:#######################\n*Game Depth is: 5\n*Player 1 is: HUMAN\n*Moves Made :\n-1-The HUMAN filled the column N:4")
            println("-2-The AI filled the column N:4")
        }
        if(scenario=="Szenario 4"){
            movecnt=0
            history.clear()
            difficulty=2

            for(loop2 in 0 ..41){
                slots[loop2].color = "white"
            }
            slots[35].color=human
            slots[38].color= ai
            slots[28].color=human
            slots[39].color= ai



            playerselector="human"

            println("#######################Test Scenario 4 Started:#######################\n*Game Depth is: 2\n*Player 1 is: HUMAN\n*Moves Made :\n-1-The Human filled the column N:1")
            println("-2-The AI filled the column N:4\n-3-The Human filled the column N:1\n-4-The AI filled the column N:5")
        }
        if(scenario=="Szenario 2"){
            movecnt=0
            history.clear()
            difficulty=3

            for(loop3 in 0 ..41){
                slots[loop3].color = "white"
            }
            slots[38].color=human
            slots[40].color= ai
            slots[41].color=human
            slots[33].color= ai
            slots[26].color=human
            slots[34].color= ai
            slots[27].color=human
            slots[20].color= ai
            slots[13].color=human
            slots[31].color= ai
            slots[24].color=human
            slots[39].color= ai
            slots[32].color=human
            slots[25].color= ai







            playerselector="human"

            println("#######################Test Scenario 2 Started#######################\n*Game Depth is: 3\n*Player 1 is: HUMAN\n*Moves Made :\n-1-The Human filled the column N:4")
            println("-2-The AI filled the column N:6\n-3-The Human filled the column N:7\n-4-The AI filled the column N:6")
            println("-5-The HUMAN filled the column N:6\n-6-The AI filled the column N:7\n-7-The HUMAN filled the column N:7")
            println("-8-The AI filled the column N:7\n-9-The HUMAN filled the column N:7\n-10-The AI filled the column N:4")
            println("-11-The HUMAN filled the column N:4\n-12-The AI filled the column N:5\n-13-The HUMAN filled the column N:5")
            println("-14-The AI filled the column N:5")
        }
        if(scenario=="Szenario 5"){
            movecnt=0
            history.clear()
            difficulty=4

            for(loop4 in 0 ..41){
                slots[loop4].color = "white"
            }
            slots[38].color=human
            slots[31].color= ai




            playerselector="human"

            println("#######################Test Scenario 5 Started:#######################\n*Game Depth is: 4\n*Player 1 is: HUMAN\n*Moves Made :\n-1-The Human filled the column N:4")
            println("-2-The AI filled the column N:4")
        }




        ctx.json(slots)
    }


    app.get("/index") { ctx: Context ->


        //the cell that the user clicked on
        val cell = ctx.queryParam("co")!!.toInt()

        if ( (checkvictory(slots)!=ai ) && (checkvictory(slots)!=human ) && (checkvictory(slots)!="tie" ) && filling(cell) == true   ) {
            if(testmod==false) {
                if (playerselector == "human") {
                    filling(cell)

                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"

                    history.add(celltofill.slot)
                }
                if (playerselector == "ai") {
                    move = bestMove()
                    filling(move)
                    history.add(celltofill.aimove)
                    slots[celltofill.aimove].color = celltofill.aicolor

                    playerselector = "human"
                }



            }


        }


        if (celltofill.color != "white" && celltofill.aicolor != "white" ) {
            ctx.json(celltofill)

        }





        if ( (checkvictory(slots)==ai ) || (checkvictory(slots)==human ) || (checkvictory(slots)=="tie" ) ) {
            celltofill.victory=true
            celltofill.winner=Static.winner
         println("before :"+ celltofill.winner)
            ctx.json(celltofill)
        }


    }

    app.get("/playmove"){ctx: Context ->

        if ( (checkvictory(slots)!=ai ) && (checkvictory(slots)!=human ) && (checkvictory(slots)!="tie" )   ) {
            if (testmod == true) {

                if (scenario=="Szenario 1") {

                    movecnt++
                    if(movecnt==1){
                        filling(41)
                        slots[celltofill.slot].color = celltofill.color

                        playerselector = "ai"


                        bestMove()
                        println("-6-The Human filled the column N:7 ")


                        if(scenario1==true){
                            println("---->Die Spiel-Engine kann im nächsten Zug gewinnen (Sichttiefe 1) :"+ scenario1)
                            scenario1=false
                        }

                    }
                    if(movecnt==2) {

                        println("-7-The AI filled the column N:3 and won The Game")
                        filling(bestMove())

                        slots[celltofill.aimove].color = celltofill.aicolor

                        playerselector = "human"
                        movecnt=0
                        println("#######################Test 1 ended#######################")
                        testmod=false
                    }
                }

            }
            if (scenario=="Szenario 4") {
                movecnt++
                if(movecnt==1){
                    filling(21)
                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"
                    //


                    bestMove()
                    println("-5-The Human filled the column N:1 ")


                    if(scenario4==true){
                        println("---->Die Spiel-Engine vereitelt eine unmittelbare Gewinnbedrohung des Gegners (Sichttiefe 2) :"+ scenario4)
                        scenario4=false
                    }

                }
                if(movecnt==2) {

                    println("-6-The AI filled the column N:1 to prevent HUMAN from winning")
                    filling(bestMove())

                    slots[celltofill.aimove].color = celltofill.aicolor

                    playerselector = "human"

                    println("#######################Test 4 ended#######################")
                    testmod=false
                }
            }

            if (scenario=="Szenario 2") {
                movecnt++
                if(movecnt==1){
                    filling(17)
                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"


                    //calling the bestmove() to check if the scenario 2 is true
                    bestMove()
                    println("-15-The Human filled the column N:4 ")


                    if(scenario2==true){
                        println("---->Die Spiel-Engine kann im übernächsten Zug gewinnen (Sichttiefe 3) :"+ scenario2)
                        scenario2=false
                    }

                }
                if(movecnt==2) {

                    println("-16-The AI filled the column N:6 ")
                    filling(bestMove())

                    slots[celltofill.aimove].color = celltofill.aicolor

                    playerselector = "human"


                }
                if(movecnt==3){
                    filling(10)
                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"




                    println("-17-The Human filled the column N:4 ")




                }
                if(movecnt==4) {

                    println("-18-The AI filled the column N:3  and won The Game")
                    filling(bestMove())

                    slots[celltofill.aimove].color = celltofill.aicolor

                    playerselector = "human"

                    println("#######################Test 2 ended#######################")
                    testmod=false
                }
            }
            if (scenario=="Szenario 3") {
                movecnt++
                if(movecnt==1){
                    filling(35)
                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"




                    println("-3-The Human filled the column N:1 ")

                    bestMove()
                    if(scenario3==true){
                        println("---->Die Spiel-Engine kann im überübernächsten Zug gewinnen (Sichttiefe 5) :"+ scenario3)
                        scenario3=false
                    }

                }
                if(movecnt==2) {

                    println("-6-The AI filled the column N:5 ")
                    filling(bestMove())

                    slots[celltofill.aimove].color = celltofill.aicolor

                    playerselector = "human"

                    println("#######################Test 3 ended#######################")
                    testmod=false
                }
            }
            if (scenario=="Szenario 5") {
                movecnt++
                if(movecnt==1){
                    filling(39)
                    slots[celltofill.slot].color = celltofill.color

                    playerselector = "ai"




                    println("-3-The Human filled the column N:5 ")



                }
                if(movecnt==2) {

                    println("-6-The AI filled the column N:1 to prevent HUMAN from winning")
                    filling(bestMove())

                    slots[celltofill.aimove].color = celltofill.aicolor
                    println("-4-The Human filled the column N:3 ")
                    if(scenario5==true){
                        println("---->Die Spiel-Engine vereitelt ein Drohung, die den Gegner im übernächsten Zug ansonsten einen Gewinn umsetzen lässt (Sichttiefe 4) :"+ scenario5)
                        scenario5=false
                    }

                    playerselector = "human"

                    println("#######################Test 5 ended#######################")
                    testmod=false
                }
            }


        }

        ctx.json(slots)
    }




// for the refresh of the  webpage
    app.get("/board") { ctx: Context ->
        if(player1=="ai"&& history.size==0 ){
            playerselector="ai"
            filling(bestMove())
            history.add(celltofill.aimove)
            slots[celltofill.aimove].color = celltofill.aicolor

            playerselector="human"
        }


        ctx.json(slots)
    }
    app.get("/undo") { ctx: Context ->
        val lastmove1:Int
        val lastmove2:Int
        celltofill.winner="no one"
        celltofill.victory=false
        if(history.size>=2){
            lastmove1= history[history.size-1]
            lastmove2= history[history.size-2]

            if(movecnt==2){
                movecnt-=2
            }
            slots[lastmove1].color="white"
            slots[lastmove2].color="white"


            history.removeAt(history.size-1)
            history.removeAt(history.size-1)

        }



        ctx.json(slots)
    }
    app.after("/undo") { ctx: Context ->
        var empty=true
        for(i in 0..41){
            if(slots[i].color!="white"){
                empty=false
            }
        }



        //if the board is empty refill it if player 1 is the ai
        if(player1=="ai" && empty==true){
            playerselector="ai"
            filling(bestMove())
            history.add(celltofill.aimove)
            slots[celltofill.aimove].color = celltofill.aicolor

            playerselector="human"
            ctx.json(slots)
        }


    }



    app.get("/restart") { ctx: Context ->
        testmod=false
        if(player1=="human") {
            playerselector = "human"
        }
        celltofill.winner="no one"
        celltofill.victory=false
        currentcolor=human
        for(i in 0..41){
            slots[i].color = "white"
        }


        history.clear()

        if(player1=="ai" ){
            playerselector="ai"
            filling(bestMove())
            history.add(celltofill.aimove)
            slots[celltofill.aimove].color = celltofill.aicolor
            playerselector="human"
        }

        ctx.json(slots)
    }
    app.get("/gotosettings"){ctx: Context ->
        ctx.redirect("choice.html")
    }



}


//new filling method
fun filling(cell: Int):Boolean{
    var index=cell
    var pos:Int=-1
    while (index>-1){

        index-=7
    }

    if(index==-1){
        label@ for(i in 41 downTo (41-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(41-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1

            }
        }
    }
    if(index==-2){
        label@  for(i in 40 downTo (40-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(40-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(index==-3){
        label@  for(i in 39 downTo (39-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(39-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(index==-4){
        label@  for(i in 38 downTo (38-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(38-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(index==-5){
        label@   for(i in 37 downTo (37-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(37-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(index==-6){
        label@  for(i in 36 downTo (36-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(36-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(index==-7){
        label@   for(i in 35 downTo (35-(6*6))+1 step 7){
            if(slots[i].color=="white"){
                pos=i
                break@label
            }
            //the column is full
            if( (i==(35-(6*6))+1)&& slots[i].color!="white" ){
                pos=-1
            }
        }
    }
    if(playerselector=="human"){
        if(pos>-1) {
            celltofill.slot = pos
            celltofill.color = human
            //   playerselector++
        }


    }
    else if (playerselector=="ai"){
        if(pos>-1) {
            celltofill.aimove = pos
            celltofill.aicolor = ai
            //  playerselectoror++

        }


    }
    if(pos==-1){
        return false
    }


    return true

}




fun checkvictory(board: MutableList<Slot>): String {
    var victory = "no one"
    var case=false
    var col=0
    var colu=0
    var sum:Int
    var boucle=0
    var count =0
    //horrizental victory

    while (boucle <=38) {

        count++



        if ((board[boucle].color != "white") && (board[boucle].color == board[boucle + 1].color) && (board[boucle].color == board[boucle + 2].color) && (board[boucle].color == board[boucle + 3].color)) {
            victory = board[boucle].color
            Static.winner = board[boucle].color
            celltofill.winslots[0]=boucle
            celltofill.winslots[1]=boucle+1
            celltofill.winslots[2]=boucle+2
            celltofill.winslots[3]=boucle+3


        }
        if(count==4){
            boucle+=3
            count=0
        }

        boucle ++
    }
    //vertical victory

        outerloop@ for (j in 41 downTo 21) {
            sum = j

            innerloop@ for (k in 0..2) {
                sum = sum - 7

                if ((board[j].color != "white") && (board[j].color == board[sum].color)) {
                    case = true
                    col = j
                } else if ((board[j].color != board[sum].color)) {
                    case = false
                    break@innerloop
                }

            }

            if (case == true) {
                victory=board[j].color

                Static.winner = board[j].color
                println(Static.winner)
                celltofill.winslots[0]=j
                celltofill.winslots[1]=j-7
                celltofill.winslots[2]=j-14
                celltofill.winslots[3]=j-21
                break@outerloop

            }
        }

    //Diagonal



        //right-side diagonal victory

        //first row
        for (j in 38 downTo 35){
            sum=j

            innerloop2@for(k in 0..2){
                sum=sum-6

                if((board[j].color != "white") && (board[j].color==board[sum].color )) {
                    colu=j

                    case=true

                }else if((board[j].color != "white") && (board[j].color != board[sum].color )){

                    case=false
                    break@innerloop2
                }


            }

            if(case==true){
                celltofill.winslots[0]=j
                celltofill.winslots[1]=j-6
                celltofill.winslots[2]=j-12
                celltofill.winslots[3]=j-18
                victory=board[colu].color
                Static.winner = board[colu].color
                return victory
            }

        }
//second row
        for (j in 31 downTo 28){
            sum=j

            innerloop3@ for(k in 0..2){
                sum=sum-6

                if((board[j].color != "white") && (board[j].color==board[sum].color )) {
                    colu=j

                    case =true

                }
                else if((board[j].color != "white") && (board[j].color != board[sum].color )){
                    case=false
                    break@innerloop3
                }


            }

            if(case==true){
                celltofill.winslots[0]=j
                celltofill.winslots[1]=j-6
                celltofill.winslots[2]=j-12
                celltofill.winslots[3]=j-18
                victory=board[colu].color
                Static.winner = board[colu].color
                return victory
            }
        }
//third row
        for (j in 24 downTo 21){
            sum=j

            innerloop4@ for(k in 0..2){
                sum=sum-6

                if((board[j].color != "white") && (board[j].color==board[sum].color )) {
                    colu=j

                    case= true

                }else if((board[j].color != "white") && (board[j].color != board[sum].color )){
                    case=false
                    break@innerloop4
                }


            }

            if(case==true){
                celltofill.winslots[0]=j
                celltofill.winslots[1]=j-6
                celltofill.winslots[2]=j-12
                celltofill.winslots[3]=j-18
                victory=board[colu].color
                Static.winner = board[colu].color
                return victory
            }
        }
        //end right-side diagonal check



        //left-side diagonal victory
        for (i in 41 downTo 27 step 7){

            for (j in i downTo i - 3) {






                if ((board[j].color != "white") && (board[j].color == board[j - 8].color) && (board[j].color == board[j - 16].color) && (board[j].color == board[j - 24].color) ) {
                    colu = j

                    case = true

                } else if ((board[j].color != "white") && ( (board[j].color != board[j - 8].color) || (board[j].color != board[j - 16].color) || (board[j].color != board[j - 24].color) ) ) {
                    case = false

                }





                if(case==true){
                    celltofill.winslots[0]=j
                    celltofill.winslots[1]=j-8
                    celltofill.winslots[2]=j-16
                    celltofill.winslots[3]=j-24
                    victory=board[colu].color

                    Static.winner = board[colu].color
                    return victory
                }



            }



            /* innerloop@ for(k in 0..2){
                 sum=sum-8

                 if((board[j].color != "white") && (board[j].color==board[sum].color )) {
                     colu=j

                     case= true

                 }else if((board[j].color != "white") && (board[j].color != board[sum].color )){
                     case=false
                     break@innerloop
                 }


             }
 */

        }



    //tie
    if( (victory!=ai) && (victory!=human) ){
        case=true
        for (i in 0..41) {
            if(slots[i].color=="white")
                case=false
        }
        if(case==true){
            victory="tie"
            Static.winner="tie"
        }




    }



    return victory

}
fun scoringboard():Int{
    var scoring=0


    //horizental
    for( i in 41 downTo 3 step 7){


        for(j in i downTo (i-3)  ) {



            if( (countHorizentalp(slots,ai,j)).get(ai)==3 && (countHorizentalp(slots,ai,j)).get("white")==1 ){
                scoring+=1000
            }
            else if( (countHorizentalp(slots, human,j)).get(human)==3 && (countHorizentalp(slots,human,j)).get("white")==1 ){
                scoring-=1000
            }
            if( (countHorizentalp(slots,ai,j)).get(ai)==2 && (countHorizentalp(slots,ai,j)).get("white")==2 ){
                scoring+=100
            }
            else if( (countHorizentalp(slots,human,j)).get(human)==2 && (countHorizentalp(slots,human,j)).get("white")==2 ){
                scoring-=100
            }
            if( (countHorizentalp(slots,ai,j)).get(ai)==1 && (countHorizentalp(slots,ai,j)).get("white")==3 ){
                scoring+=10
            }
            else if( (countHorizentalp(slots,human,j)).get(human)==1 && (countHorizentalp(slots,human,j)).get("white")==3 ){
                scoring-=10
            }

        }

    }

    //vertical
    for (i in 41 downTo 21) {




        if( (countverticalp(slots,ai,i).get(ai)==3) && (countverticalp(slots,ai,i).get("white")==1) ){
            scoring+=1000
        }
        else if( (countverticalp(slots,human,i).get(human)==3) && (countverticalp(slots,human,i).get("white")==1) ){
            scoring-=1000
        }


        if( (countverticalp(slots,ai,i).get(ai)==2) && (countverticalp(slots,ai,i).get("white")==2) ){
            scoring+=100
        }
        else if( (countverticalp(slots,human,i).get(human)==2) && (countverticalp(slots,human,i).get("white")==2) ){
            scoring-=100
        }
        if( (countverticalp(slots,ai,i).get(ai)==1) && (countverticalp(slots,ai,i).get("white")==3) ){
            scoring+=10
        }
        else if( (countverticalp(slots,human,i).get(human)==1) && (countverticalp(slots,human,i).get("white")==3) ){
            scoring-=10
        }

    }


    //Diag right
    for (i in 38 downTo 21 step 7) {
        for(j in i downTo (i-3)){



            if( (countDiagRightp(slots,ai,j)).get(ai)==3 && (countDiagRightp(slots,ai,j)).get("white")==1 ){
                scoring+=1000
            }
            else if( (countDiagRightp(slots,human,j)).get(human)==3 && (countDiagRightp(slots,human,j)).get("white")==1 ){
                scoring-=1000
            }
            if( (countDiagRightp(slots,ai,j)).get(ai)==2 && (countDiagRightp(slots,ai,j)).get("white")==2 ){
                scoring+=100
            }
            else if( (countDiagRightp(slots,human,j)).get(human)==2 && (countDiagRightp(slots,human,j)).get("white")==2 ){
                scoring-=100
            }
            if( (countDiagRightp(slots,ai,j)).get(ai)==1 && (countDiagRightp(slots,ai,j)).get("white")==3 ){
                scoring+=10
            }
            else if( (countDiagRightp(slots,human,j)).get(human)==1 && (countDiagRightp(slots,human,j)).get("white")==3 ){
                scoring-=10
            }

        }

    }

    //Diag Left
    for (i in 41 downTo 27 step 7) {
        for (j in i downTo i - 3) {
            if ((countDiagLeftp(slots, ai, j)).get(ai) == 3 && (countDiagLeftp(slots, ai, j)).get("white") == 1) {
                scoring += 1000
            } else if ((countDiagLeftp(slots, human, j)).get(human) == 3 && (countDiagLeftp(slots, human, j)).get("white") == 1) {
                scoring -= 1000
            }
            if ((countDiagLeftp(slots, ai, j)).get(ai) == 2 && (countDiagLeftp(slots, ai, j)).get("white") == 2) {
                scoring += 100
            }
            else if ((countDiagLeftp(slots, human, j)).get(human) == 2 && (countDiagLeftp(slots, human, j)).get("white") == 2) {
                scoring -= 100
            }
            if( (countDiagLeftp(slots,ai,j)).get(ai)==1 && (countDiagLeftp(slots,ai,j)).get("white")==3 ){
                scoring+=10
            }
            else if( (countDiagLeftp(slots,human,j)).get(human)==1 && (countDiagLeftp(slots,human,j)).get("white")==3 ){
                scoring-=10
            }

        }

    }



    return scoring
}

fun countHorizentalp(board: MutableList<Slot>, player:String ,j : Int): HashMap<String, Int> {
    var hashMap: HashMap<String, Int> = HashMap<String, Int>()
    hashMap.put(player, 0)
    hashMap.put("white", 0)
    var k = j
    var whitecnt = 0
    var playercnt = 0
    if (k >= 3) {
        while (k >= (j - 3)) {
            if (board[k].color == player) {
                playercnt++
                hashMap.set(player, playercnt)

            }
            if (board[k].color == "white") {
                whitecnt++
                hashMap.set("white", whitecnt)
            }




            k--
        }


    }
    return hashMap
}

fun countverticalp(board: MutableList<Slot>, player:String ,j : Int): HashMap<String, Int> {
    var hashMap : HashMap<String, Int> = HashMap<String, Int> ()
    hashMap.put(player , 0)
    hashMap.put("white" , 0)

    var whitecnt=0
    var playercnt=0


    for (k in j downTo (j-(7*3)) step 7) {
        if(board[k].color==player){
            playercnt++
            hashMap.set(player,playercnt)
        }
        if(board[k].color=="white"){
            whitecnt++
            hashMap.set("white",whitecnt)
        }

    }







    return hashMap
}


fun countDiagRightp(board: MutableList<Slot>, player:String ,j : Int): HashMap<String, Int> {
    var hashMap : HashMap<String, Int> = HashMap<String, Int> ()
    hashMap.put(player , 0)
    hashMap.put("white" , 0)

    var whitecnt=0
    var playercnt=0
    for(k in j downTo j-(6*3) step 6){
        if(board[k].color==player){
            playercnt++
            hashMap.set(player,playercnt)
        }
        if(board[k].color=="white"){
            whitecnt++
            hashMap.set("white",whitecnt)
        }

    }


    return hashMap
}
fun countDiagLeftp(board: MutableList<Slot>, player:String ,j : Int): HashMap<String, Int> {
    var hashMap : HashMap<String, Int> = HashMap<String, Int> ()
    hashMap.put(player , 0)
    hashMap.put("white" , 0)

    var whitecnt=0
    var playercnt=0


    for(k in j downTo j-(8*3) step 8){
        if(board[k].color==player){
            playercnt++
            hashMap.set(player,playercnt)
        }
        if(board[k].color=="white"){
            whitecnt++
            hashMap.set("white",whitecnt)
        }

    }



    return hashMap
}

//******************************************************// //starting with minimax// //***************************************************************************//

fun bestMove():Int {

    // AI to make its turn
    var bestScore:Int = Int.MIN_VALUE
    var move=0
    var index:Int
    var score :Int
    var boardcpy =slots.toMutableList()


    // Is the spot available?
    for (j in 0..6 ) {
        // Is the spot available?
        index= EmptySpace(boardcpy).get(j)



        if (index>=0) {

            boardcpy[index].color = ai
            //the ai can win in the next move
            if(checkvictory(boardcpy)=="red"){
                scenario1=true
            }




            score = minimax(boardcpy, difficulty, false,1)!!

            boardcpy[index].color = "white"

            if (score > bestScore) {
                bestScore = score
                move = index

            }

        }
    }


    return move
}


fun minimax(board:MutableList<Slot>, depth:Int, isMaximizing:Boolean,moves :Int): Int? {

    var result = checkvictory(board)
    var temp:Int


    if (result ==ai|| result== human) {
       //sceario 2 ist true wenn der AI im übernäachsten zug gewinnen kann
        if(result=="red" ){

            scenario2=true
        }
        if(result=="red" && depth== difficulty-4){
            scenario3=true
        }
        //scenario 5 ist true wenn HUMAN ein  strategischer Zug gemacht hat der ihm erlaubt im übernächsten Zug zu gewinnen wenn der AI ihn nicht verhindert
        if(result==human && depth ==difficulty-3){
            scenario5=true

        }

        return (scores.get(result))!!-20*moves

    }
    if (result =="tie"){

        return 0-50*moves

    }
    if(depth==0){
        return scoringboard()
    }

    if (isMaximizing) {
        var bestScore = Int.MIN_VALUE

        // Is the spot available?

        for (j in 0..6 ) {


            temp= EmptySpace(board).get(j)
            if (temp>=0) {
                board[temp].color = ai

                val    score = minimax(board, depth - 1, false,moves+1)!!
                board[temp].color = "white"

                if(score>bestScore){
                    bestScore=score
                }
            }

        }
        return bestScore
    }
    else {
        var  bestScore = Int.MAX_VALUE



        for (j in 0..6 ) {
            // Is the spot available
            temp= EmptySpace(board).get(j)
            if (temp>=0) {
                board[temp].color = human
                // scenario 2
                if(checkvictory(board)=="yellow"){
                    scenario4=true
                }
                val   score = minimax(board, depth - 1, true,moves+1)!!
                board[temp].color = "white"
                if(score <bestScore){
                    bestScore=score
                }

            }
        }

        return bestScore
    }
}

//finds the next space (from the bottom)

fun EmptySpace(board:MutableList<Slot>): MutableList<Int> {
    var list: MutableList<Int> = ArrayList()

    for (i in 41 downTo 35 ) {
        label@  for(j in i downTo i-(7*5) step 7){
            if(board[j].color=="white"){
                list.add(j)
                break@label
            }
            else if( (j==i-(7*5)) && (board[j].color!="white") ){
                list.add(-1)
            }
        }


    }
    return list
}





//creating a static variable

class Static {
    companion object {
        var winner = "none"
    }
}

////////////////////////////////////////////


