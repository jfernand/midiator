package gui.node

import WaveformGenerator
import generator.DebugGenerator
import gui.Node

class DebugNode : Node("Debug") {

    init {
        createInput("debug", "Debug")
    }

    override fun buildAndLink(): WaveformGenerator {
        val debug = DebugGenerator()
        debug.link("debug", getLink("debug")!!.buildAndLink())
        return debug
    }
}
