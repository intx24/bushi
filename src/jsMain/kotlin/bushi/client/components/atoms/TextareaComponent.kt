package bushi.client.components.atoms

import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.events.Event
import react.RProps
import react.dom.textArea
import react.dom.value
import react.functionalComponent


external interface TextareaComponentProps : RProps {
    var id: String
    var value: String
    var placeholder: String?
    var required: Boolean?
    var maxLength: Number?
    var onChangeFunction: ((Event) -> Unit)?

}

val TextareaComponent = functionalComponent<TextareaComponentProps> { props ->
    textArea(classes = "textarea") {
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