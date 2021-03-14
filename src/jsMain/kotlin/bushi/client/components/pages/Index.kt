package bushi.client.components.pages

import bushi.client.components.organisms.FormSectionComponent
import bushi.client.components.organisms.TableSectionComponent
import bushi.client.components.organisms.TitleSectionComponent
import bushi.client.domain.DefinitionViewModel
import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.*
import react.dom.main

private val scope = MainScope()

val Index = functionalComponent<RProps> { _ ->
    val (stateDefinitionViewModelList, setStateDefinitionViewModelList) = useState(emptyList<DefinitionViewModel>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setStateDefinitionViewModelList(
                listOf(
                    DefinitionViewModel(
                        trigger = "trigger1",
                        name = "name1",
                        responseText = "responseText1",
                        iconEmoji = "iconEmoji1",
                        iconUrl = ":myIcon:"
                    ),
                    DefinitionViewModel(
                        trigger = "trigger2",
                        name = "name2",
                        responseText = "responseText2ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                        iconEmoji = "iconEmoji2",
                        iconUrl = "https://upload.wikimedia.org/wikipedia/commons/3/3f/JPEG_example_flower.jpg"
                    )
                )
            )
        }
    }

    main {
        child(TitleSectionComponent)
        child(
            FormSectionComponent,
            props = jsObject {
                definitionViewModelList = stateDefinitionViewModelList
                definitionViewModelListSetter = setStateDefinitionViewModelList
            }
        )
        child(
            TableSectionComponent,
            props = jsObject {
                definitionViewModelList = stateDefinitionViewModelList
            }
        )
    }
}
