package gui

import WaveformGenerator
import gui.node.AdderNode
import gui.node.DebugNode
import gui.node.FrequencyNode
import gui.node.HitVolumeControlNode
import gui.node.OutputNode
import gui.node.SineNode
import gui.node.SquareWaveGeneratorNode
import gui.node.TriangleNode
import gui.node.VariableNode
import gui.node.VolumeNode
import plusAssign
import java.awt.Dimension
import java.awt.Frame
import java.util.function.*
import javax.swing.JDialog
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class InstrumentCreationDialog(parent: Frame, onSuccess: (WaveformGenerator) -> Unit) : JDialog(parent, true) {

    private val content = LinkedTileContainer()

    init {
        this.size = Dimension(800, 600)
        val contextMenu = JPopupMenu()
        contextMenu += createItem(content, "New Adder Node", ::AdderNode)
        contextMenu += createItem(content, "New Debug Node", ::DebugNode)
        contextMenu += createItem(content, "New Output Node") {
            OutputNode {
                onSuccess.invoke(it)
                this.dispose()
            }
        }

        contextMenu += JPopupMenu.Separator()

        contextMenu += createItem(content, "New SquareWave Node", ::SquareWaveGeneratorNode)
        contextMenu += createItem(content, "New Triangle Node", ::TriangleNode)
        contextMenu += createItem(content, "New Sine Node", ::SineNode)

        contextMenu += JPopupMenu.Separator()

        contextMenu += createItem(content, "New Frequency Node", ::FrequencyNode)
        contextMenu += createItem(content, "New Variable Node", ::VariableNode)
        contextMenu += createItem(content, "New Volume Node", ::VolumeNode)
        contextMenu += createItem(content, "New Hit Volume Control Node", ::HitVolumeControlNode)

        content.componentPopupMenu = contextMenu
        this.contentPane = content
    }

    private fun createItem(content: LinkedTileContainer, text: String, action: () -> Node): JMenuItem {
        val item = JMenuItem(text)
        item.addActionListener {
            val addition = action()
            content.add(addition)
            content.revalidate()
            addition.repaint()
        }
        return item
    }
}
