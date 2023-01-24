var playcase=true
var col;
var cas=true;
var win_slots=[]
var xml = new XMLHttpRequest();
var testcases=false
function sendRequestGET(path = '', query = '') {

    xml.open('GET', path + '?' + query);
    xml.send();


}

xml.onreadystatechange = function(){
if(this.readyState==4 && this.status==200){

    if(testcases==false){
        try{
var myobj=JSON.parse(this.responseText);
}
catch{

}


document.getElementById(myobj.slot.toString()).style.backgroundColor = myobj.color.toString();
if ( myobj.winner!="yellow"){
document.getElementById(myobj.aimove.toString()).style.backgroundColor = myobj.aicolor.toString();
}


cas=true;

if( ( myobj.victory==true ) && (myobj.winner!="tie") ){
   win_slots=[myobj.winslots[0].toString(),myobj.winslots[1].toString(),myobj.winslots[2].toString(),myobj.winslots[3].toString()]; 
  
  
    document.getElementById(myobj.winslots[0].toString()).innerHTML="W"; document.getElementById(myobj.winslots[1].toString()).innerHTML="W";
    document.getElementById(myobj.winslots[2].toString()).innerHTML="W"; document.getElementById(myobj.winslots[3].toString()).innerHTML="W";
   
if (confirm(myobj.winner +" has won! \n replay ??")) {

}    

}
else if( (myobj.victory==true)&&(myobj.winner=="tie") ){

if (confirm(" It's a Tie! \n replay ??")) {

}    

}




}

}

}

     function sendcolumn(column) {
        if(cas==true){
        sendRequestGET('index', 'co=' + column);
        }
        cas=false;
    }
    function loadInnerTable() {
         loadBoard('board');
        
}
function restart(){
    loadBoard('restart');
    document.getElementById("test").style.visibility = "hidden";
    testcases=false;
   
   if(win_slots[0]!=null){
    document.getElementById(win_slots[0].toString()).innerHTML=""; document.getElementById(win_slots[1].toString()).innerHTML="";
    document.getElementById(win_slots[2].toString()).innerHTML=""; document.getElementById(win_slots[3].toString()).innerHTML="";
}
cas=false;
}
function undomove(){
    loadBoard('undo');
   
   if(win_slots[0]!=null){
    document.getElementById(win_slots[0].toString()).innerHTML=""; document.getElementById(win_slots[1].toString()).innerHTML="";
    document.getElementById(win_slots[2].toString()).innerHTML=""; document.getElementById(win_slots[3].toString()).innerHTML="";
}
}


  var http = new XMLHttpRequest();

  function loadBoard(path = '', query = '') {
    http.open('GET', path + '?' + query);
    http.send();
    if(performance.navigation.type == 2){
   location.reload(true);
}
  
  }

http.onreadystatechange = function () {
  if(this.readyState==4 && this.status==200){
    var board=JSON.parse(this.responseText);
var i;
for(i=0;i<42;i++){
    document.getElementById(i.toString()).style.backgroundColor = board[i].color;

}
cas=true;
playcase=true;


  }
 

}

function selecttest(test){
    testcases=true;
    document.getElementById("test").style.visibility = "visible";
   loadBoard('testcase', 'tst=' + test);
    if(win_slots[0]!=null){
    document.getElementById(win_slots[0].toString()).innerHTML=""; document.getElementById(win_slots[1].toString()).innerHTML="";
    document.getElementById(win_slots[2].toString()).innerHTML=""; document.getElementById(win_slots[3].toString()).innerHTML="";
    
}

}
function playmove(){
  if(playcase==true){
    loadBoard('playmove');
}playcase=false
}
