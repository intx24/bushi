package bushi.client.components.organisms

import bushi.client.components.atoms.ButtonComponent
import bushi.client.components.molecules.TextFieldComponent
import bushi.client.components.molecules.TextareaFieldComponent
import bushi.client.domain.DefinitionViewModel
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.form
import react.dom.section
import react.dom.span

external interface FormSectionComponentProps : RProps {
    var definitionViewModelList: List<DefinitionViewModel>
    var definitionViewModelListSetter: RSetState<List<DefinitionViewModel>>
}

val FormSectionComponent = functionalComponent<FormSectionComponentProps> { props ->
    val (stateTriggerValue, setStateTriggerValue) = useState("")
    val (stateNameValue, setStateNameValue) = useState("")
    val (stateResponseValue, setStateResponseValue) = useState("")
    val (stateIconEmojiValue, setStateIconEmojiValue) = useState("")
    val (stateIconUrlValue, setStateIconUrlValue) = useState("")

    val (stateTriggerErrorMessage, setStateTriggerErrorMessage) = useState("")
    val (stateResponseErrorMessage, setStateResponseErrorMessage) = useState("")
    val (stateSubmitErrorMessage, setStateSubmitErrorMessage) = useState("")

    val validateTriggerValue: (String) -> Boolean = {
        val isBlank = it.isBlank()
        val isExists = props.definitionViewModelList.any { d -> d.trigger == it }

        when {
            isBlank -> {
                setStateTriggerErrorMessage("cannot set blank trigger")
                false
            }
            isExists -> {
                setStateTriggerErrorMessage("already exists")
                false
            }
            else -> {
                setStateTriggerErrorMessage("")
                true
            }
        }
    }

    val onChangeTriggerInput: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value.trim()
        setStateTriggerValue(value)
        validateTriggerValue(value)
    }

    val onChangeNameInput: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value.trim()
        setStateNameValue(value)
    }

    val validateResponseValue: (String) -> Boolean = {
        when {
            it.isBlank() -> {
                val errorMessage = "cannot set blank response"
                setStateResponseErrorMessage(errorMessage)
                false
            }
            else -> {
                setStateResponseErrorMessage("")
                true
            }
        }
    }

    val onChangeResponseTextarea: (Event) -> Unit = {
        val value = (it.target as HTMLTextAreaElement).value.trim()
        setStateResponseValue(value)
        validateResponseValue(value)
    }

    val onChangeIconEmojiInput: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value.trim()
        setStateIconEmojiValue(value)
    }

    val clearForm: () -> Unit = {
        setStateTriggerValue("")
        setStateNameValue("")
        setStateResponseValue("")
        setStateIconEmojiValue("")
        setStateIconUrlValue("")
        setStateTriggerErrorMessage("")
        setStateResponseErrorMessage("")
        setStateSubmitErrorMessage("")
    }

    val submit: (Event) -> Unit = {
        var error = ""
        // validation on submit
        val isAllValuesAreValid = validateTriggerValue(stateTriggerValue) && validateResponseValue(stateResponseValue)
        if (!isAllValuesAreValid) {
            error += "some of form values are invalid."
        }

        // DEBUG: console
        console.log("trigger", stateTriggerValue)
        console.log("name", stateNameValue)
        console.log("response", stateResponseValue)
        console.log("iconEmoji", stateIconEmojiValue)

        // TODO: call api save method
        

        if (error.isBlank()) {
            clearForm()
            // update definition list on view
            props.definitionViewModelListSetter(
                props.definitionViewModelList.plus(
                    listOf(
                        DefinitionViewModel(
                            trigger = stateTriggerValue,
                            name = stateNameValue,
                            responseText = stateResponseValue,
                            iconEmoji = stateIconEmojiValue,
                            iconUrl = stateIconUrlValue,
                        )
                    )
                )
            )
        } else {
            setStateSubmitErrorMessage(error)
        }
    }

    section(classes = "section has-background-primary") {
        div(classes = "container") {
            form {
                attrs {
                    action = "#"
                }
                div(classes = "columns") {
                    div(classes = "column is-one-third") {
                        child(
                            TextFieldComponent,
                            props = jsObject {
                                labelProps = jsObject {
                                    labelFor = "triggerInput"
                                    text = "Trigger (required)"
                                    errorMessage = stateTriggerErrorMessage
                                }
                                textInputProps = jsObject {
                                    id = "triggerInput"
                                    value = stateTriggerValue
                                    required = true
                                    maxLength = 20
                                    onChangeFunction = onChangeTriggerInput
                                }
                            }
                        )
                    }
                }
                div(classes = "columns") {
                    div(classes = "column is-one-third") {
                        child(
                            TextFieldComponent,
                            props = jsObject {
                                labelProps = jsObject {
                                    labelFor = "nameInput"
                                    text = "Name (optional)"
                                }
                                textInputProps = jsObject {
                                    id = "nameInput"
                                    value = stateNameValue
                                    maxLength = 20
                                    onChangeFunction = onChangeNameInput
                                }
                            }
                        )
                    }
                }
                div(classes = "columns") {
                    div(classes = "column is-two-thirds") {
                        child(
                            TextareaFieldComponent,
                            props = jsObject {
                                labelProps = jsObject {
                                    labelFor = "responseInput"
                                    text = "Response Text (required)"
                                    errorMessage = stateResponseErrorMessage
                                }
                                textareaProps = jsObject {
                                    id = "responseInput"
                                    value = stateResponseValue
                                    required = true
                                    maxLength = 20
                                    onChangeFunction = onChangeResponseTextarea
                                }
                            }
                        )
                    }
                }
                div(classes = "columns is-align-items-flex-end") {
                    div(classes = "column is-two-thirds") {
                        child(
                            TextFieldComponent,
                            props = jsObject {
                                labelProps = jsObject {
                                    labelFor = "iconEmojiInput"
                                    text = "Icon (optional)"
                                }
                                textInputProps = jsObject {
                                    id = "iconEmojiInput"
                                    value = stateIconEmojiValue
                                    onChangeFunction = onChangeIconEmojiInput
                                }
                            }
                        )
                    }
                    div(classes = "column") {
                        child(
                            ButtonComponent,
                            props = jsObject {
                                type = ButtonType.button
                                enabled = false
                                additionalClasses = "is-rounded"
                                text = "UPLOAD FILE"
                            }
                        )
                    }
                }
                div(classes = "columns") {
                    div(classes = "column") {
                        child(
                            ButtonComponent,
                            props = jsObject {
                                type = ButtonType.button
                                text = "SUBMIT"
                                additionalClasses = "is-rounded"
                                onClickFunction = submit
                            }
                        )
                    }
                }
                div(classes = "columns") {
                    div(classes = "column") {
                        span(classes = "has-text-danger") {
                            +stateSubmitErrorMessage
                        }
                    }
                }
            }
        }
    }
}