package bushi.client.components.organisms

import bushi.client.components.atoms.TableBodyRowComponent
import bushi.client.components.atoms.TableBodyRowComponentProps
import react.RProps
import react.child
import react.dom.*
import react.functionalComponent

external interface TableComponentProps : RProps {
    var rowPropsList: List<TableBodyRowComponentProps>
}

val TableComponent = functionalComponent<TableComponentProps> { props ->
    table(classes = "table is-fullwidth has-background-white-bis") {
        thead {
            tr {
                th {
                    +"Trigger"
                }
                th {
                    +"Name"
                }
                th {
                    +"Response"
                }
                th {
                    +"Icon"
                }
            }
        }
        tbody {
            props.rowPropsList.forEach { rp ->
                child(
                    TableBodyRowComponent,
                    props = rp
                )
            }
        }
    }
}