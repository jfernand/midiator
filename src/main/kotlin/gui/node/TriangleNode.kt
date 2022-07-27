package gui.node

import WaveformGenerator
import generator.TriangleGenerator
import gui.Node

class TriangleNode : Node("Triangle Generator") {

    init {
        createInput("frequency", "Frequency")
    }

    override fun buildAndLink(): WaveformGenerator {
        val gen = TriangleGenerator()
        gen.link("frequency", getLink("frequency")!!.buildAndLink())
        return gen
    }
}
