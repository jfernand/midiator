package generator

import WaveformGenerator

class SquareWaveGenerator : WaveformGenerator {

    private lateinit var frequencyFunction: WaveformGenerator
    private lateinit var highTimeGenerator: WaveformGenerator

    override fun link(linkType: String, generator: WaveformGenerator) {
        when (linkType) {
            "frequency" -> frequencyFunction = generator
            "highTime" -> highTimeGenerator = generator
            else -> throw IllegalArgumentException("Unknown link type: '$linkType'")
        }
    }

    override fun validate() = ::frequencyFunction.isInitialized && ::highTimeGenerator.isInitialized

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        val resultLength = activation.synth.getSamplesPerFrame()

        val highTimeProfile = highTimeGenerator.generate(activation)
        val frequencyProfile = frequencyFunction.generate(activation)
        return DoubleArray(size = resultLength) {
            val phase =
                ((timeStamp + it * dT - activation.hitTime) * frequencyProfile[it] * 2 * Math.PI) % (2 * Math.PI)
            if (phase < Math.PI * (highTimeProfile[it] / 0.5))
                return@DoubleArray 1.0
            else
                return@DoubleArray -1.0
        }
    }

    override fun update(activation: WaveformGenerator.Activation) =
        frequencyFunction.update(activation) && highTimeGenerator.update(activation)
}
