package components.organisms

import components.atoms.bushiLabel
import kotlinx.html.*
import kotlinx.html.js.onInputFunction
import kotlinx.html.js.onSubmitFunction

fun HtmlBlockTag.registrationForm(block: FORM.() -> Unit) {
    var errorMessage1 = ""
    var errorMessage2 = ""
    form {
        action = "#"
        onSubmitFunction = { console.log("test") }
        div(classes = "row") {
            bushiLabel("triggerInput", "Trigger (required)", errorMessage1)
            textInput(classes = "u-full-width") {
                id = "triggerInput"
                onInputFunction = {}
                required = true
            }
        }
        div(classes = "row") {
            bushiLabel("responseInput", "Response Text (required)", errorMessage2)
            textInput(classes = "u-full-width") {
                id = "triggerInput"
                onInputFunction = { event -> console.log(event) }
                required = true
            }
        }
        div(classes = "row") {
            bushiLabel("nameInput", "Name (optional)")
            textInput(classes = "u-full-width") {
                id = "nameInput"
            }
        }
        div(classes = "row") {
            bushiLabel("iconEmojiInput", "Icon (optional)")
            div(classes = "seven columns") {
                button(classes = "u-full-width") {
                    type = ButtonType.button
                    disabled = true
                    +"Upload file"
                }
            }
        }
        div(classes = "row") {
            submitInput(classes = "button-primary") {
                value = "submit"
            }
        }
        div(classes = "row") {
            span(classes = "error_message u-full-width") {
                +"error"
            }
        }
    }
}