package generator

import WaveformGenerator
import java.lang.Math.random
import java.util.*
import kotlin.math.PI

class NoiseGenerator(val sequenceLength: Int) : WaveformGenerator { //rec: Math.pow(2.0, 12.0).toInt()

    private val random = DoubleArray(sequenceLength) { random() * 2 - 1 }
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
            val phase = ((timeStamp + it * dT) * frequencyProfile[it] * 2 * PI) % (2 * PI)
            val index = (sequenceLength * (phase % (2 * PI)) / (2 * PI)).toInt()
            random[index]
        }
    }

    override fun update(activation: WaveformGenerator.Activation) = frequencyFunction.update(activation)
}
