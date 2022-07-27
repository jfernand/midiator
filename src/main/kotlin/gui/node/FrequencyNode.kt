package gui.node

import generator.FrequencyReaderGenerator
import gui.Node

class FrequencyNode : Node("Frequency") {
    companion object {
        private val generator = FrequencyReaderGenerator()
    }

    override fun buildAndLink() = generator
}
