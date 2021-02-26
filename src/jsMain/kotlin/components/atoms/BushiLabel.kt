package components.atoms

import kotlinx.html.HtmlBlockTag
import kotlinx.html.label
import kotlinx.html.span

fun HtmlBlockTag.bushiLabel(labelFor: String, text: String, errorMessage: String? = null) {
    label {
        htmlFor = labelFor
        +text
        if (!errorMessage.isNullOrBlank()) {
            span(classes = "error_message") {
                +errorMessage
            }
        }
    }
}