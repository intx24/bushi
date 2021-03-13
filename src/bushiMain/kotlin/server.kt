import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*

fun HTML.index() {
    head {
        title("Bushi Triggers")
        meta {
            charset = "UTF-8"
        }
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }
        link(
            href = "https://cdn.jsdelivr.net/npm/bulma@0.9.1/css/bulma.min.css",
            rel = "stylesheet",
        )
    }
    body {
        div {
            id = "root"
        }
        script(src = "/static/output.js") {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}