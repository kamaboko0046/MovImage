package com.kamaboko.imageavoid.customView

data class PaintData(
    var setX: Float,
    var setY: Float,
    var col: Int) {

    var oriX: Float = 0F
    var oriY: Float = 0F
    var accelerationX: Int = 0
    var accelerationY: Int = 0
    var dst:Float = 0F
    var oriDst: Float = 0F

    init{
        oriX = setX
        oriY = setY
        accelerationX = 0
        accelerationY = 0
        dst = (200..300).random().toFloat()
        oriDst = dst
    }

}