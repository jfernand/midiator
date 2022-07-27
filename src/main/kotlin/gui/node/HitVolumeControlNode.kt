package gui.node

import HitVolumeControl
import WaveformGenerator
import gui.Node

class HitVolumeControlNode : Node("Hit Volume") {
    private val attackTime = createNumberInput("attack", "Attack time:")
    private val decayTime = createNumberInput("decay", "Decay time:")
    private val stopTime = createNumberInput("stop", "Stop time:")
    private val sustain = createNumberInput("sustain", "Sustain level:")

    override fun buildAndLink(): WaveformGenerator {
        val generator = HitVolumeControl()
        generator.link("attack", getLink("attack")!!.buildAndLink())
        generator.link("decay", getLink("decay")!!.buildAndLink())
        generator.link("stop", getLink("stop")!!.buildAndLink())
        generator.link("sustain", getLink("sustain")!!.buildAndLink())
        return generator
    }
}
