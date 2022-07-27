import Main.generator
import Main.listener
import Main.synth
import dev.atsushieno.ktmidi.MidiChannelStatus
import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.OnMidiReceivedEventListener
import dev.atsushieno.ktmidi.RtMidiAccess
import gui.InstrumentCreationDialog
import kotlinx.coroutines.runBlocking
import java.awt.Button
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.sound.sampled.AudioFormat
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

object Main {
    private const val bitDepth = 16
    private const val samplesPerSecond = 44000f
    private const val channels = 1
    private const val signed = true
    private const val bigEndian = false

    val synth = Synthesizer(AudioFormat(samplesPerSecond, bitDepth, channels, signed, bigEndian))
    var generator: WaveformGenerator? = null
    val listener = object : KeyListener {

        private val keyState = HashMap<Int, WaveformGenerator.Activation>()

        override fun keyPressed(e: KeyEvent) {
            val specialHandler = SpecialKeys[e.extendedKeyCode]
            if (specialHandler != null && (keyState[e.extendedKeyCode] == null) && generator != null) {
                val activation = WaveformGenerator.Activation(synth, 0.0, generator!!) //Dummy
                keyState[e.extendedKeyCode] = activation
                specialHandler.keyDown(synth)
                if (SpecialKeys.canRegister(e.extendedKeyCode))
                    synth.recorder.registerEvent(e)
            }
            val frequency = Generators[e.extendedKeyCode]
            if (frequency != null && (keyState[e.extendedKeyCode] == null) && generator != null) {
                val activation = WaveformGenerator.Activation(synth, frequency, generator!!)
                keyState[e.extendedKeyCode] = activation
                synth.activate(activation)
                synth.recorder.registerEvent(e)
            }
        }

        override fun keyReleased(e: KeyEvent) {
            val activation = keyState[e.extendedKeyCode]
            if (activation != null) {
                activation.releaseTime = activation.synth.getTimeStamp()
            }
            keyState.remove(e.extendedKeyCode)
            SpecialKeys[e.extendedKeyCode]?.keyUp(synth)
            if (Generators[e.extendedKeyCode] != null || SpecialKeys.canRegister(e.extendedKeyCode))
                synth.recorder.registerEvent(e)
        }

        override fun keyTyped(e: KeyEvent?) {}
    }
}

fun main(args: Array<String>) {
    println("Keyboard synth.")

    synth.startup()
    Runtime.getRuntime().addShutdownHook(Thread(synth::shutdown))
    val frame = JFrame("Keyboard Synth")
    frame.size = Dimension(400, 50)

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher {
        if (it.id == KeyEvent.KEY_PRESSED) {
            listener.keyPressed(it)
        } else if (it.id == KeyEvent.KEY_RELEASED) {
            listener.keyReleased(it)
        }
        return@addKeyEventDispatcher false
    }

    val content = JPanel()
    content.layout = FlowLayout()

    content += button("Create Instrument") {
        SwingUtilities.invokeLater {
            val dialog = InstrumentCreationDialog(frame) {
                generator = it
            }
            dialog.isVisible = true
        }
    }

    content += button("Save Instrument") {
        SwingUtilities.invokeLater {
            val dialog = JFileChooser()
            val state = dialog.showSaveDialog(frame)
            if (state == JFileChooser.APPROVE_OPTION) {
                val file = dialog.selectedFile
                val out = ObjectOutputStream(FileOutputStream(file))
                out.writeObject(generator)
            }
        }
    }

    content += button("Load Instrument") {
        SwingUtilities.invokeLater {
            val dialog = JFileChooser()
            val state = dialog.showOpenDialog(frame)
            if (state == JFileChooser.APPROVE_OPTION) {
                val file = dialog.selectedFile
                val input = ObjectInputStream(FileInputStream(file))
                generator = input.readObject() as WaveformGenerator
            }
        }
    }

    frame.contentPane = content
    frame.focusTraversalKeysEnabled = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}

fun button(title: String, actionListener: () -> Unit): Button {
    val ret = Button(title)
    ret.addActionListener { actionListener() }
    return ret
}

operator fun Container.plusAssign(component: Component) {
    this.add(component)
}

fun oldmain(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val access = RtMidiAccess()
    for (port in access.inputs) {
        println("${port.id} ${port.name} ${port.manufacturer}")
    }

    runBlocking {
        val input = access.openInputAsync("0")
        input.setMessageReceivedListener(
            object : OnMidiReceivedEventListener {
                override fun onEventReceived(data: ByteArray, start: Int, length: Int, timestampInNanoseconds: Long) {
//                    println("$data, $start, $length, $timestampInNanoseconds")
                    val event = MidiEvent.convert(data, start, length)
                    event.forEach {
                        when (it.eventType) {
                            MidiChannelStatus.CC.toByte() -> print("CC")
                            MidiChannelStatus.CAF.toByte() -> print("CAF")
                            MidiChannelStatus.NOTE_OFF.toByte() -> print("OFF")
                            MidiChannelStatus.NOTE_ON.toByte() -> print("ON")
                            MidiChannelStatus.PAF.toByte() -> print("PAF")
                            MidiChannelStatus.NRPN.toByte() -> print("NRPN")
                            MidiChannelStatus.PER_NOTE_ACC.toByte() -> print("PER_NOTE_ACC")
                            MidiChannelStatus.PER_NOTE_MANAGEMENT.toByte() -> print("PER_NOTE_MANAGEMENT")
                            MidiChannelStatus.PER_NOTE_PITCH_BEND.toByte() -> print("PER_NOTE_PITCH_BEND")
                            MidiChannelStatus.PER_NOTE_RCC.toByte() -> print("PER_NOTE_RCC")
                            MidiChannelStatus.PITCH_BEND.toByte() -> print("PITCH_BEND")
                            MidiChannelStatus.PROGRAM.toByte() -> print("PROGRAM")
                            MidiChannelStatus.RPN.toByte() -> print("RPN")
                            MidiChannelStatus.RELATIVE_NRPN.toByte() -> print("RELATIVE_NRPN")
                            MidiChannelStatus.RELATIVE_RPN.toByte() -> print("RELATIVE_RPN")
                            else -> print("%x".format(it.eventType))
                        }
                        when (it.value) {

                        }
                        println()
                    }
                }
            })
    }
    while (true) {
    }
}
