package generator

import WaveformGenerator

class AdderGenerator : WaveformGenerator {

    private val generators = HashMap<String, WaveformGenerator>()

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val profiles = HashMap<String, DoubleArray>()
        generators.forEach {
            profiles[it.key] = it.value.generate(activation)
        }
        return DoubleArray(activation.synth.getSamplesPerFrame()) { index -> profiles.values.sumOf { profile -> profile[index] } }
    }

    override fun update(activation: WaveformGenerator.Activation): Boolean {
        return generators.all { it.value.update(activation) }
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        generators[linkType] = generator
    }

    override fun validate() = generators.all { it.value.validate() }
}
