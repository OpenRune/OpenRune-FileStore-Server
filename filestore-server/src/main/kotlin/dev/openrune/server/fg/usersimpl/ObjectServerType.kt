package dev.openrune.server.fg.usersimpl

import dev.openrune.server.fg.backend.impl.ObjectServerBase

//CUSTOM TYPE THAT PEOPLE CAN MAKE THEIR OWN POJO
data class ObjectServerType(
    override var id: Int = -1,
    override var width: Int = 1,
    var customThing : Boolean= false
) : ObjectServerBase