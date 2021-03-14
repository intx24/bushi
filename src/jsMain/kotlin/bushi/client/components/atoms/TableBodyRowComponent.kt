package bushi.client.components.atoms

import react.RProps
import react.dom.a
import react.dom.td
import react.dom.tr
import react.functionalComponent

external interface TableBodyRowComponentProps : RProps {
    var trigger: String
    var name: String
    var response: String
    var iconEmoji: String
    var iconUrl: String
}

val TableBodyRowComponent = functionalComponent<TableBodyRowComponentProps> { props ->
    val isImageUrl = props.iconUrl.startsWith("http")
    tr {
        td {
            +props.trigger
        }
        td {
            +props.name
        }
        td {
            +props.response
        }
        if (isImageUrl) {
            td {
                a(href = props.iconUrl) {
                    +props.iconEmoji
                }
            }
        } else {
            td {
                +props.iconEmoji
            }
        }
    }
}