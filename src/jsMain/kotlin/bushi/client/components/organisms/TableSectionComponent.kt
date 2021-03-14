package bushi.client.components.organisms

import bushi.client.components.atoms.TableBodyRowComponentProps
import bushi.client.components.atoms.TextInputComponent
import bushi.client.domain.DefinitionViewModel
import kotlinext.js.jsObject
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RProps
import react.child
import react.dom.div
import react.dom.section
import react.functionalComponent
import react.useState

external interface TableSectionComponentProps : RProps {
    var definitionViewModelList: List<DefinitionViewModel>
}

val TableSectionComponent = functionalComponent<TableSectionComponentProps> { props ->
    val (stateTriggerFilterValue, setStateTriggerFilterValue) = useState("")

    val getFilteredRowPropsList: () -> List<TableBodyRowComponentProps> = {
        props.definitionViewModelList.filter { d ->
            d.trigger.contains(stateTriggerFilterValue)
        }.map { d ->
            val subStringedResponse =
                if (d.responseText.length > 30) "${d.responseText.substring(0, 31)} ..." else d.responseText
            jsObject {
                trigger = d.trigger
                name = d.name
                response = subStringedResponse
                iconEmoji = d.iconEmoji
                iconUrl = d.iconUrl
            }
        }
    }

    val onChangeTriggerFilterInput: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value.trim()
        setStateTriggerFilterValue(value)
    }

    section(classes = "section has-background-white-bis") {
        div(classes = "container") {
            div(classes = "columns") {
                div(classes = "column is-offset-two-thirds is-one-third") {
                    child(
                        TextInputComponent,
                        props = jsObject {
                            value = stateTriggerFilterValue
                            placeholder = "filter triggers"
                            onChangeFunction = onChangeTriggerFilterInput
                        }
                    )
                }
            }
            child(
                TableComponent,
                props = jsObject {
                    rowPropsList = getFilteredRowPropsList()
                }
            )
        }
    }
}

