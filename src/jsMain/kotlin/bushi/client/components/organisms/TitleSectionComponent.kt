package bushi.client.components.organisms

import bushi.client.components.atoms.TitleComponent
import kotlinext.js.jsObject
import react.RProps
import react.child
import react.dom.div
import react.dom.section
import react.functionalComponent

val TitleSectionComponent = functionalComponent<RProps> { _ ->
    section(classes = "section has-background-white-bis") {
        div(classes = "container") {
            child(
                TitleComponent,
                props = jsObject {
                    title = "Bushi"
                }
            )
        }
    }
}