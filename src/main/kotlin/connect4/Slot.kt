package connect4

class Slot {
    var slot:Int=0;
    var color:String="white"
    var victory:Boolean=false
    var winner:String="no one"
    var aimove:Int=0
    var aicolor:String="white"
    var winslots= IntArray(4){0}
}