package bushi.client.components.atoms

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.events.Event
import react.RProps
import react.dom.input
import react.functionalComponent


external interface TextInputComponentProps : RProps {
    var id: String
    var value: String
    var placeholder: String?
    var required: Boolean?
    var maxLength: Number?
    var onChangeFunction: ((Event) -> Unit)?
}

val TextInputComponent = functionalComponent<TextInputComponentProps> { props ->
    input(InputType.text, classes = "input") {
        attrs {
            id = props.id
            value = props.value
            placeholder = props.placeholder.orEmpty()
            required = props.required ?: false
            maxLength = props.maxLength.takeIf { it != null }?.toString() ?: ""
            onChangeFunction = props.onChangeFunction ?: {}
        }
    }
}