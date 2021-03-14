package bushi.client.components.atoms

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RProps
import react.dom.input
import react.functionalComponent
import react.useState


external interface TextInputComponentProps : RProps {
    var id: String
    var value: String
    var placeholder: String?
    var required: Boolean?
    var maxLength: Number?
    var onChangeFunction: ((Event) -> Unit)?
}

val TextInputComponent = functionalComponent<TextInputComponentProps> { props ->
    val (stateValue, setStateValue) = useState(props.value)

    val changeHandler: (Event) -> Unit = {
        props.onChangeFunction?.let { itFun -> itFun(it) }
        val value = (it.target as HTMLInputElement).value
        setStateValue(value)
    }

    input(InputType.text, classes = "input") {
        attrs {
            id = props.id
            value = stateValue
            placeholder = props.placeholder.orEmpty()
            required = props.required ?: false
            maxLength = props.maxLength.takeIf { it != null }?.toString() ?: ""
            onChangeFunction = changeHandler
        }
    }
}