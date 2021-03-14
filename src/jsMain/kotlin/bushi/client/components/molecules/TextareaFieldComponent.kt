package bushi.client.components.molecules

import bushi.client.components.atoms.LabelComponent
import bushi.client.components.atoms.LabelComponentProps
import bushi.client.components.atoms.TextareaComponent
import bushi.client.components.atoms.TextareaComponentProps
import react.RProps
import react.child
import react.dom.div
import react.functionalComponent

external interface TextareaFieldComponentProps : RProps {
    var labelProps: LabelComponentProps
    var textareaProps: TextareaComponentProps
}

val TextareaFieldComponent = functionalComponent<TextareaFieldComponentProps> { props ->
    div(classes = "field") {
        child(
            LabelComponent,
            props = props.labelProps
        )
        div(classes = "control") {
            child(
                TextareaComponent,
                props = props.textareaProps
            )
        }
    }
}