package bushi.client.components.molecules

import bushi.client.components.atoms.LabelComponent
import bushi.client.components.atoms.LabelComponentProps
import bushi.client.components.atoms.TextInputComponent
import bushi.client.components.atoms.TextInputComponentProps
import react.RProps
import react.child
import react.dom.div
import react.functionalComponent

external interface TextFieldComponentProps : RProps {
    var labelProps: LabelComponentProps
    var textInputProps: TextInputComponentProps
}

val TextFieldComponent = functionalComponent<TextFieldComponentProps> { props ->
    div(classes = "field") {
        child(
            LabelComponent,
            props = props.labelProps
        )
        div(classes = "control") {
            child(
                TextInputComponent,
                props = props.textInputProps
            )
        }
    }
}