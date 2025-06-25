package dev.openrune.server.fg.backend.impl

import dev.openrune.definition.Definition

interface ObjectServerBase : Definition {
    override var id: Int
    val width: Int



    //Some Custom Functions maybe code to encode default shit ?
}