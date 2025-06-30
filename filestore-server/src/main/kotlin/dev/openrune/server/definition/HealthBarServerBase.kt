package dev.openrune.server.definition

import dev.openrune.definition.Definition

abstract class HealthBarServerBase : Definition {
    override var id: Int = 0
    open var width: Int = 30

    fun barWidth(curHealth : Int, maxHealth : Int): Int {
        return ((curHealth.toDouble() / maxHealth) * width).toInt()
    }

}