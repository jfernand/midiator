package gui.node

import WaveformGenerator
import generator.SineGenerator
import gui.Node

class SineNode : Node("Sinus Generator") {

    init {
        createInput("frequency", "Frequency")
    }

    override fun buildAndLink(): WaveformGenerator {
        val gen = SineGenerator()
        gen.link("frequency", getLink("frequency")!!.buildAndLink())
        return gen
    }
}
