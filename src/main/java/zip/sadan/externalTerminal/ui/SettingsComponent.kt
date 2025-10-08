package zip.sadan.externalTerminal.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.dispatcher.SingleEventDispatcher
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

private class ObservableSetting<T>(private var initialValue: T) : ObservableMutableProperty<T> {
    private val dispatcher = SingleEventDispatcher.create<T>()
    override fun set(value: T) {
        initialValue = value
        dispatcher.fireEvent(initialValue)
    }

    override fun get(): T = initialValue

    override fun afterChange(parentDisposable: Disposable?, listener: (T) -> Unit) {
        dispatcher.whenEventHappened(parentDisposable, listener)
    }

}

private fun <T> observableSetting(initialValue: T): ObservableMutableProperty<T> = ObservableSetting(initialValue)

class SettingsComponent {
    object Model {
        var _path = observableSetting("")
        var path by _path
    }

    val panel = panel {
        row("External terminal path:") {
            textFieldWithBrowseButton()
                .bindText(Model._path)
        }
    }
}