package generator

import WaveformGenerator

class DebugGenerator : WaveformGenerator {

    private lateinit var parentNode: WaveformGenerator

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val res = parentNode.generate(activation)
        println("Debug node report: min=${res.minOrNull()}, max=${res.maxOrNull()}, average=${res.average()}")
        return res
    }

    override fun update(activation: WaveformGenerator.Activation): Boolean {
        return parentNode.update(activation)
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        if (linkType == "debug") {
            this.parentNode = generator
        } else {
            throw RuntimeException("Unknown link type '$linkType'.")
        }
    }

    override fun validate() = ::parentNode.isInitialized
}
