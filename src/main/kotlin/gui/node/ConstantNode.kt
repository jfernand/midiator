package gui.node

import WaveformGenerator
import generator.ConstantGenerator
import gui.Node

class ConstantNode : Node("Constant") {

    init {
        createNumberInput("value", "Value:")
    }

    override fun buildAndLink(): WaveformGenerator {
        val generator = ConstantGenerator(1.0)
        generator.link("frequency", getLink("frequency")!!.buildAndLink())
        return generator
    }
}
