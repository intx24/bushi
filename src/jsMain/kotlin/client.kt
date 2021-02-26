import components.organisms.registrationForm
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import org.w3c.dom.Node

fun main() {
    window.onload = {
        document.body?.append {
            div(classes = "container") {
                div(classes = "row") {
                    h1 {
                        +"Bushi"
                    }
                }
                registrationForm { }
            }
        }
    }
}

fun Node.sayHello() {
    append {
        div {
            +"Hello from JS"
        }
    }
}