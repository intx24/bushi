package bushi.client.components.atoms

import react.RProps
import react.dom.h1
import react.functionalComponent


external interface TitleComponentProps : RProps {
    var title: String
}

val TitleComponent = functionalComponent<TitleComponentProps> { props ->
    h1(classes = "title is-1") {
        +props.title
    }

}