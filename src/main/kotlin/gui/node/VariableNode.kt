package gui.node

import generator.VariableReadoutGenerator
import gui.Node

class VariableNode : Node("Variable") {

    private val variableName = createTextInput("Name")

    override fun buildAndLink() = VariableReadoutGenerator(variableName.text)
}
