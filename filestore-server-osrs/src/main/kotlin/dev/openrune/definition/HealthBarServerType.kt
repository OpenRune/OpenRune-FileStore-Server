package dev.openrune.definition

import dev.openrune.server.definition.HealthBarServerBase


data class HealthBarServerType(
    override var id: Int = -1,
    override var width: Int = 30,
) : HealthBarServerBase()