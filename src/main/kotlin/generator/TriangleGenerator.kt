package generator

import WaveformGenerator
import java.util.*

class TriangleGenerator : WaveformGenerator {

    private lateinit var frequencyFunction: WaveformGenerator

    override fun link(linkType: String, generator: WaveformGenerator) {
        if (linkType.lowercase(Locale.getDefault()) == "frequency") {
            this.frequencyFunction = generator
        } else {
            throw IllegalArgumentException("Unknown link type: '$linkType'")
        }
    }

    override fun validate() = this::frequencyFunction.isInitialized

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        val resultLength = activation.synth.getSamplesPerFrame()

        val frequencyProfile = frequencyFunction.generate(activation)
        return DoubleArray(size = resultLength) {
            val phase =
                ((timeStamp + it * dT - activation.hitTime) * frequencyProfile[it] * 2 * Math.PI) % (2 * Math.PI)
            return@DoubleArray Math.abs(2 * (Math.PI - phase) / Math.PI) - 1
        }
    }

    override fun update(activation: WaveformGenerator.Activation) = frequencyFunction.update(activation)
}
