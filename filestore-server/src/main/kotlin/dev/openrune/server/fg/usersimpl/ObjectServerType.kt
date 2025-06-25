package dev.openrune.server.fg.usersimpl

import dev.openrune.server.fg.backend.impl.ObjectServerBase

//CUSTOM TYPE THAT PEOPLE CAN MAKE THEIR OWN POJO
data class ObjectServerType(
    override var id: Int,
    override val width: Int = 1,
    val customThing : Int= 1
) : ObjectServerBase