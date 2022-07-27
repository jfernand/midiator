package gui.node

import WaveformGenerator
import generator.SquareWaveGenerator
import gui.Node

class SquareWaveGeneratorNode : Node("Squarewave") {

    init {
        createInput("frequency", "Frequency")
        createNumberInput("highTime", "High time:")
    }

    override fun buildAndLink(): WaveformGenerator {
        val generator = SquareWaveGenerator()
        generator.link("highTime", this.getLink("highTime")!!.buildAndLink())
        generator.link("frequency", this.getLink("frequency")!!.buildAndLink())
        return generator
    }
}
