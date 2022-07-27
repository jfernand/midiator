package gui.node

import VolumeControl
import WaveformGenerator
import gui.Node

class VolumeNode : Node("Volume") {

    init {
        createInput("waveform", "Signal")
        createInput("volume", "Volume")
    }

    override fun buildAndLink(): WaveformGenerator {
        val generator = VolumeControl()
        generator.link("waveform", this.getLink("waveform")!!.buildAndLink())
        generator.link("volume", this.getLink("volume")!!.buildAndLink())
        return generator
    }
}
