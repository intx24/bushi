package bushi.client.components.atoms

import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.RProps
import react.dom.button
import react.functionalComponent


external interface ButtonComponentProps : RProps {
    var type: ButtonType
    var text: String
    var enabled: Boolean?
    var additionalClasses: String?
    var onClickFunction: ((Event) -> Unit)?
}

val ButtonComponent = functionalComponent<ButtonComponentProps> { props ->
    var classes = "button"
    if (!props.additionalClasses.isNullOrBlank()) {
        classes = "$classes ${props.additionalClasses}"
    }

    button(classes = classes, type = props.type) {
        attrs {
            disabled = props.enabled.takeIf { it != null } == false
            onClickFunction = props.onClickFunction ?: {}
        }
        +props.text
    }
}