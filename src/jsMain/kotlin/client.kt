import components.atoms.bushiLabel
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onInputFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.Node

data class Entry(
    val trigger: String,
    val name: String,
    val response: String,
    val icon: String
)

fun main() {
    window.onload = {
        document.body?.main()
    }
}

fun Node.main() {

    val entries = listOf<Entry>(
        Entry(
            "trigger",
            "name",
            "response",
            "icon",
        )
    )

    append {
        main {
            section(classes = "section") {
                div(classes = "container") {
                    h1(classes = "title is-1") {
                        +"Bushi"
                    }
                }
            }
            section(classes = "section has-background-primary") {
                div(classes = "container") {
                    form {
                        action = "#"
                        onSubmitFunction = { console.log("test") }
                        div(classes = "columns") {
                            div(classes = "column is-one-third") {
                                div(classes = "field") {
                                    bushiLabel("triggerInput", "Trigger (required)", "")
                                    div(classes = "control") {
                                        textInput(classes = "input") {
                                            id = "triggerInput"
                                            placeholder = ":placeholder:"
                                            onInputFunction = {}
                                            required = true
                                            maxLength = "20"
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "columns") {
                            div(classes = "column is-one-third") {
                                div(classes = "field") {
                                    bushiLabel("nameInput", "Name (optional)")
                                    div(classes = "control") {
                                        textInput(classes = "input") {
                                            id = "nameInput"
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "columns") {
                            div(classes = "column is-two-thirds") {
                                div(classes = "field") {
                                    bushiLabel("responseInput", "Response Text (required)", "")
                                    div(classes = "control") {
                                        textArea(classes = "textarea") {
                                            id = "responseInput"
                                            onInputFunction = { event -> console.log(event) }
                                            required = true
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "columns") {
                            div(classes = "column is-two-thirds") {
                                div(classes = "field") {
                                    bushiLabel("iconEmojiInput", "Icon (optional)", "")
                                    div(classes = "control") {
                                        textInput(classes = "input") {
                                            id = "iconEmojiInput"
                                            onInputFunction = { event -> console.log(event) }
                                            required = true
                                        }
                                    }
                                }
                            }
                            div(classes = "column mt-auto") {
                                button(classes = "button") {
                                    type = ButtonType.button
                                    disabled = true
                                    +"UPLOAD FILE"
                                }
                            }
                        }
                        div(classes = "columns") {
                            div(classes = "column") {
                                button(classes = "button is-success is-light") {
                                    type = ButtonType.submit
                                    +"SUBMIT"
                                }
                            }
                        }
                        div(classes = "columns") {
                            div(classes = "column") {
                                span(classes = "has-text-danger") {
                                    +"error"
                                }
                            }
                        }
                    }
                }
            }
            section(classes = "section") {
                div(classes = "container") {
                    div(classes = "columns") {
                        div(classes = "column is-offset-two-thirds is-one-third") {
                            textInput(classes = "input") {
                                placeholder = "filter triggers"
                                onInputFunction = {}
                            }
                        }
                    }
                    div(classes = "table-container") {
                        table(classes = "table is-fullwidth") {
                            thead {
                                tr {

                                    th {
                                        +"Trigger"
                                    }
                                    th {
                                        +"Name"
                                    }
                                    th {
                                        +"Icon"
                                    }
                                    th {
                                        +"Responsive"
                                    }
                                }
                            }
                            tbody {
                                entries.forEach { e ->
                                    tr {
                                        td {
                                            +e.trigger
                                        }
                                        td {
                                            +e.name
                                        }
                                        td {
                                            +e.response
                                        }
                                        td {
                                            +e.icon
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Node.titleSection() {
    append {
        section(classes = "section") {
            div(classes = "container") {
                h1(classes = "title is-1") {
                    +"Bushi"
                }
            }
        }
    }
}

fun Node.listSection() {
    append {
        section(classes = "section") {
            div(classes = "container") {
                div(classes = "columns") {
                    div(classes = "column is-one-third") {
                        textInput(classes = "input") {
                            id = "triggerInput"
                            placeholder = ":placeholder:"
                            onInputFunction = {}
                            required = true
                            maxLength = "20"
                        }
                    }
                }
            }
        }
    }
}