package bushi.client.components.atoms

import react.RProps
import react.dom.label
import react.dom.span
import react.functionalComponent

external interface LabelComponentProps : RProps {
    var labelFor: String
    var text: String
    var errorMessage: String?
}

val LabelComponent = functionalComponent<LabelComponentProps> { props ->
    label(classes = "label") {
        attrs {
            // there is issue about this attribute https://github.com/JetBrains/kotlin-wrappers/issues/365
            htmlFor = props.labelFor
        }
        +props.text
        span(classes = "has-text-danger") {
            +" ${props.errorMessage.orEmpty()}"
        }
    }
}